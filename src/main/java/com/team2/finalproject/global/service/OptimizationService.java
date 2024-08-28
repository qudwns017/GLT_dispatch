package com.team2.finalproject.global.service;

import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.dto.response.CoordinatesResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.CourseDetailResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.CourseResponse;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.dto.request.OrderRequest;
import com.team2.finalproject.domain.transportorder.model.dto.request.TransportOrderRequest;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicle.repository.VehicleRepository;
import com.team2.finalproject.domain.vehicledetail.model.entity.VehicleDetail;
import com.team2.finalproject.domain.vehicledetail.repository.VehicleDetailRepository;
import com.team2.finalproject.global.util.Util;
import com.team2.finalproject.global.util.request.OptimizationRequest;
import com.team2.finalproject.global.util.response.OptimizationResponse;
import com.team2.finalproject.global.util.response.ResultStopover;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptimizationService {

    private final WebClient.Builder webClientBuilder;
    private final DeliveryDestinationRepository deliveryDestinationRepository;
    private final SmRepository smRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleDetailRepository vehicleDetailRepository;

    public List<CourseResponse> callOptimizationApi(TransportOrderRequest request, List<OptimizationRequest> optimizationRequests, List<Long> smIdOrder, Map<String, String[]> addressMapping) {
        WebClient webClient = webClientBuilder.build();
        String apiUrl = "http://43.201.58.61:8080/api/Optimization";

        // 최적화 API 호출
        List<OptimizationResponse> responses = webClient.post()
                .uri(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(optimizationRequests)
                .retrieve()
                .bodyToFlux(OptimizationResponse.class)
                .collectList()
                .block();

        if (responses == null || responses.isEmpty()) {
            throw new RuntimeException("Failed to optimize route");
        }

        List<CourseResponse> courses = new ArrayList<>();

        for (int i = 0; i < responses.size(); i++) {
            OptimizationResponse response = responses.get(i);
            Long smId = smIdOrder.get(i); // 최적화 요청의 순서를 기준으로 smId를 가져옴
            CourseResponse courseResponse = processOptimizationResponse(request, response, smId, addressMapping);
            courses.add(courseResponse);
        }

        return courses;
    }

    // 경로 하나에 대한 작업
    public CourseResponse processOptimizationResponse(TransportOrderRequest request, OptimizationResponse optimizationResponse, Long smId, Map<String, String[]> addressMapping) {
        // smId에 해당하는 요청을 필터링하여 별도의 리스트 생성
        List<OrderRequest> smSpecificRequests = request.orderReuquestList().stream()
                .filter(order -> order.smId().equals(smId))
                .toList();

        // smSpecificRequests를 Map으로 변환하여 address로 매핑
        Map<String, List<OrderRequest>> orderRequestMap = smSpecificRequests.stream()
                .collect(Collectors.groupingBy(
                        order -> order.address() + " " + order.detailAddress() // Map의 키로 사용할 address 필드
                ));

        Sm sm = smRepository.findByIdOrThrow(smId);
        Vehicle vehicle = vehicleRepository.findBySm(sm);
        VehicleDetail vehicleDetail = vehicleDetailRepository.findByVehicle(vehicle);

        boolean errorYn = false; // 전체 경로에 대한 오류 여부를 나타내는 플래그

        double totalWeight = 0;
        double totalVolume = 0;

        // CourseDetailResponse 리스트 생성
        List<CourseDetailResponse> courseDetailResponseList = new ArrayList<>();
        for (ResultStopover stopover : optimizationResponse.getResultStopoverList()) {
            // stopover의 도로명 주소와 매칭되는 OrderRequest를 찾음
            List<OrderRequest> matchingOrders = orderRequestMap.get(stopover.getAddress());
            OrderRequest matchingOrder = matchingOrders.remove(0);

            String[] addressParts = Util.splitAddress(stopover.getAddress());
            String address = addressParts[0];
            String detailAddress = addressParts[1];

            // 배송처(경유지)별로 진입 불가 톤코드를 검사
            DeliveryDestination destination = deliveryDestinationRepository.findByFullAddress(address, detailAddress);
            boolean isRestricted = false;
            if (destination != null) {
                isRestricted = checkRestrictedTonCode(vehicleDetail.getVehicleCode(), destination.getRestrictedTonCode());
            }

            // 하나라도 제한된 톤코드와 일치하면 전체 플래그를 true로 설정
            if (isRestricted) {
                errorYn = true;
            }

            // 주문의 총 중량과 부피를 계산
            totalWeight += matchingOrder.weight();
            totalVolume += matchingOrder.volume();

            // CourseDetailResponse 생성
            CourseDetailResponse detailResponse = CourseDetailResponse.builder()
                    .errorYn(isRestricted)
                    .ett(stopover.getTimeFromPrevious() / 1000 / 60)  // 예상 이동 시간 (초를 분으로 변환)
                    .expectationOperationStartTime(stopover.getEndTime())
                    .expectationOperationEndTime(Util.addDelayTime(stopover.getEndTime(), stopover.getDelayTime()))
                    .lat(stopover.getLat())
                    .lon(stopover.getLon())
                    .distance(stopover.getDistance() / 1000.0)
                    .address(addressMapping.get(stopover.getAddress())[0])
                    .detailAddress(addressMapping.get(stopover.getAddress())[1])
                    .expectedServiceDuration(Util.convertLocalTimeToMinutes(stopover.getDelayTime()))
                    .deliveryDestinationId(destination != null ? destination.getId() : 0)  // 배송처 ID
                    .managerName(destination != null ? destination.getAdminName() : null)  // 담당자명
                    .phoneNumber(destination != null ? destination.getPhoneNumber() : null)  // 담당자전화번호
                    .deliveryType(matchingOrder.deliveryType())  // 배송유형 ("지입", "택배")
                    .smId(smId)  // 기사 ID
                    .smName(sm.getSmName())  // 기사명
                    .shipmentNum(matchingOrder.shipmentNum())  // 운송장 번호
                    .clientOrderKey(matchingOrder.clientOrderKey())  // 업체주문번호
                    .orderType(matchingOrder.orderType())  // 주문유형 ("배송", "수거")
                    .receivedDate(matchingOrder.receivedDate())  // 주문 접수일
                    .serviceRequestDate(matchingOrder.serviceRequestDate())  // 작업 희망일
                    .serviceRequestTime(matchingOrder.serviceRequestTime())  // 희망 도착 시간
                    .clientName(matchingOrder.clientName())  // 고객명
                    .contact(matchingOrder.contact())  // 고객연락처
                    .zipcode(matchingOrder.zipcode())  // 우편번호
                    .volume(matchingOrder.volume())  // 볼륨
                    .weight(matchingOrder.weight())  // 중량
                    .note(matchingOrder.note())  // 고객전달사항
                    .productName(matchingOrder.productName())  // 상품명
                    .productCode(matchingOrder.productCode())  // 상품 코드
                    .productQuantity(matchingOrder.productQuantity())  // 상품 수량
                    .build();

            courseDetailResponseList.add(detailResponse);
        }

        // 용적률 계산
        int floorAreaRatio = 0;
        String deliveryType = courseDetailResponseList.get(0).getDeliveryType();

        if ("지입".equals(deliveryType)) {
            // 지입인 경우 중량을 기반으로 용적률 계산
            floorAreaRatio = (int)(totalWeight / vehicle.getMaxLoadWeight() * 100);
        } else if ("택배".equals(deliveryType)) {
            // 택배인 경우 부피를 기반으로 용적률 계산
            floorAreaRatio = (int)(totalVolume / vehicle.getMaxLoadVolume() * 100);
        }

        // 경로의 좌표 리스트 생성
        List<CoordinatesResponse> coordinatesResponseList = optimizationResponse.getCoordinates().stream()
                .map(coordinate -> CoordinatesResponse.builder()
                        .lon(coordinate.getLon())
                        .lat(coordinate.getLat())
                        .build())
                .toList();

        // 최종 DispatchListResponse 객체를 반환
        return CourseResponse.builder()
                .errorYn(errorYn)
                .smName(courseDetailResponseList.get(0).getSmName())
                .smPhoneNumber(sm.getUsers().getPhoneNumber())
                .tonCode(vehicleDetail.getVehicleCode())
                .ton(vehicleDetail.getVehicleTon())
                .orderNum(optimizationResponse.getResultStopoverList().size())  // 주문 수 = 경유지 수
                .mileage((int) optimizationResponse.getTotalDistance() / 1000)  // 주행 거리
                .totalTime(optimizationResponse.getTotalTime())  // 주행 시간
                .floorAreaRatio(floorAreaRatio)
                .courseDetailResponseList(courseDetailResponseList)
                .coordinatesResponseList(coordinatesResponseList)
                .build();
    }

    // 진입 불가 톤코드 검사
    private boolean checkRestrictedTonCode(String vehicleCode, String restrictedTonCode) {
        if (restrictedTonCode == null || restrictedTonCode.isEmpty()) {
            return false;
        }
        String[] restrictedCodes = restrictedTonCode.split(",");
        for (String code : restrictedCodes) {
            if (vehicleCode.equals(code.trim())) {
                return true;
            }
        }
        return false;
    }
}
