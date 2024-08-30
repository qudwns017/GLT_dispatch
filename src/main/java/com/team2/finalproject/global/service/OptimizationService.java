package com.team2.finalproject.global.service;

import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.dto.response.CourseResponse;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.dto.request.OrderRequest;
import com.team2.finalproject.domain.transportorder.model.dto.request.TransportOrderRequest;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicle.repository.VehicleRepository;
import com.team2.finalproject.domain.vehicledetail.model.entity.VehicleDetail;
import com.team2.finalproject.domain.vehicledetail.repository.VehicleDetailRepository;
import com.team2.finalproject.global.util.TransportOrderUtil;
import com.team2.finalproject.global.util.request.OptimizationRequest;
import com.team2.finalproject.global.util.response.OptimizationResponse;
import com.team2.finalproject.global.util.response.ResultStopover;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OptimizationService {

    private final DeliveryDestinationRepository deliveryDestinationRepository;
    private final SmRepository smRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleDetailRepository vehicleDetailRepository;
    private final WebClient webClient;

    public OptimizationService(DeliveryDestinationRepository deliveryDestinationRepository,
                               SmRepository smRepository, VehicleRepository vehicleRepository,
                               VehicleDetailRepository vehicleDetailRepository,
                               @Value("${optimization-api.uri}")String uri){
        this.deliveryDestinationRepository = deliveryDestinationRepository;
        this.smRepository = smRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleDetailRepository = vehicleDetailRepository;
        this.webClient = WebClient.builder().baseUrl(uri).build();
    }

    public List<CourseResponse> callOptimizationApi(TransportOrderRequest request,
                                                    List<OptimizationRequest> optimizationRequests,
                                                    List<Long> smIdOrder, Map<String, String[]> addressMapping) {
        // 최적화 API 호출
        List<OptimizationResponse> responses = webClient.post()
                .uri("/api/Optimization")
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
            // 하나의 경로 응답 생성
            CourseResponse courseResponse = createCourseResponse(request, smIdOrder.get(i), responses.get(i), addressMapping);
            courses.add(courseResponse);
        }

        return courses;
    }

    // 하나의 경로에 대한 작업
    private CourseResponse createCourseResponse(TransportOrderRequest request, Long smId,
                                                OptimizationResponse response, Map<String, String[]> addressMapping) {
        Sm sm = smRepository.findByIdOrThrow(smId);
        Vehicle vehicle = vehicleRepository.findBySm(sm);
        VehicleDetail vehicleDetail = vehicleDetailRepository.findByVehicle(vehicle);

        // smId를 필터링하여 해당 기사의 주문 리스트 받아오기
        Map<String, List<OrderRequest>> orderRequestMap = mapOrdersWithAddresses(request, smId);
        List<CourseResponse.CourseDetailResponse> courseDetailResponseList =
                createCourseDetailResponseList(response.getResultStopoverList(), orderRequestMap, vehicleDetail, addressMapping);

        int floorAreaRatio = calculateFloorAreaRatio(vehicle, courseDetailResponseList);
        List<CourseResponse.CoordinatesResponse> coordinatesResponseList = mapCoordinates(response);

        return buildCourseResponse(sm, vehicleDetail, response, floorAreaRatio, courseDetailResponseList, coordinatesResponseList);
    }

    private Map<String, List<OrderRequest>> mapOrdersWithAddresses(TransportOrderRequest request, Long smId) {
        return request.orderReuquestList().stream()
                .filter(order -> order.smId().equals(smId))
                .collect(Collectors.groupingBy(order -> order.address() + " " + order.detailAddress()));
    }

    private List<CourseResponse.CourseDetailResponse> createCourseDetailResponseList(List<ResultStopover> stopovers,
                                                                                     Map<String, List<OrderRequest>> orderRequestMap,
                                                                                     VehicleDetail vehicleDetail,
                                                                                     Map<String, String[]> addressMapping) {
        List<CourseResponse.CourseDetailResponse> courseDetailResponseList = new ArrayList<>();
        boolean errorYn = false;

        for (ResultStopover stopover : stopovers) {
            // 앞서 만든 특정 기사의 주문 리스트에서 도로명 주소가 매칭되는 주문 불러오기
            OrderRequest matchingOrder = findMatchingOrder(orderRequestMap, stopover.getAddress());
            DeliveryDestination destination = findDestination(stopover);

            // 배송처(경유지)별로 진입 불가 톤코드를 검사
            boolean isRestricted = checkRestrictedTonCode(vehicleDetail.getVehicleCode(),
                    destination != null ? destination.getRestrictedTonCode() : null);

            if (isRestricted) {
                errorYn = true;
            }

            courseDetailResponseList.add(createCourseDetailResponse(stopover, matchingOrder, destination, isRestricted, addressMapping));
        }

        return courseDetailResponseList;
    }

    private OrderRequest findMatchingOrder(Map<String, List<OrderRequest>> orderRequestMap, String address) {
        List<OrderRequest> matchingOrders = orderRequestMap.get(address);
        return matchingOrders.remove(0);
    }

    private DeliveryDestination findDestination(ResultStopover stopover) {
        String[] addressParts = TransportOrderUtil.splitAddress(stopover.getAddress());
        return deliveryDestinationRepository.findByFullAddress(addressParts[0], addressParts[1]);
    }

    private CourseResponse.CourseDetailResponse createCourseDetailResponse(ResultStopover stopover, OrderRequest order,
                                                                           DeliveryDestination destination,
                                                                           boolean isRestricted,
                                                                           Map<String, String[]> addressMapping) {
        return CourseResponse.CourseDetailResponse.builder()
                .errorYn(isRestricted)
                .ett(stopover.getTimeFromPrevious() / 1000 / 60)
                .expectationOperationStartTime(stopover.getEndTime())
                .expectationOperationEndTime(TransportOrderUtil.addDelayTime(stopover.getEndTime(), stopover.getDelayTime()))
                .lat(stopover.getLat())
                .lon(stopover.getLon())
                .distance(stopover.getDistance() / 1000.0)
                .address(addressMapping.get(stopover.getAddress())[0])
                .detailAddress(addressMapping.get(stopover.getAddress())[1])
                .expectedServiceDuration(TransportOrderUtil.convertLocalTimeToMinutes(stopover.getDelayTime()))
                .deliveryDestinationId(destination != null ? destination.getId() : 0)
                .managerName(destination != null ? destination.getManagerName() : null)
                .phoneNumber(destination != null ? destination.getPhoneNumber() : null)
                .deliveryType(order.deliveryType())
                .smId(order.smId())
                .smName(order.smName())
                .shipmentNum(order.shipmentNum())
                .clientOrderKey(order.clientOrderKey())
                .orderType(order.orderType())
                .receivedDate(order.receivedDate())
                .serviceRequestDate(order.serviceRequestDate())
                .serviceRequestTime(order.serviceRequestTime())
                .clientName(order.clientName())
                .contact(order.contact())
                .zipcode(order.zipcode())
                .volume(order.volume())
                .weight(order.weight())
                .note(order.note())
                .productName(order.productName())
                .productCode(order.productCode())
                .productQuantity(order.productQuantity())
                .build();
    }

    private int calculateFloorAreaRatio(Vehicle vehicle, List<CourseResponse.CourseDetailResponse> courseDetailResponseList) {
        double totalWeight = courseDetailResponseList.stream().mapToDouble(CourseResponse.CourseDetailResponse::getWeight).sum();
        double totalVolume = courseDetailResponseList.stream().mapToDouble(CourseResponse.CourseDetailResponse::getVolume).sum();

        String deliveryType = courseDetailResponseList.get(0).getDeliveryType();
        if ("지입".equals(deliveryType)) {
            return (int) (totalWeight / vehicle.getMaxLoadWeight() * 100);
        } else if ("택배".equals(deliveryType)) {
            return (int) (totalVolume / vehicle.getMaxLoadVolume() * 100);
        }

        return 0;
    }

    private List<CourseResponse.CoordinatesResponse> mapCoordinates(OptimizationResponse response) {
        return response.getCoordinates().stream()
                .map(coordinate -> CourseResponse.CoordinatesResponse.builder()
                        .lon(coordinate.getLon())
                        .lat(coordinate.getLat())
                        .build())
                .toList();
    }

    private CourseResponse buildCourseResponse(Sm sm, VehicleDetail vehicleDetail, OptimizationResponse response,
                                               int floorAreaRatio,
                                               List<CourseResponse.CourseDetailResponse> courseDetailResponseList,
                                               List<CourseResponse.CoordinatesResponse> coordinatesResponseList) {
        return CourseResponse.builder()
                .errorYn(courseDetailResponseList.stream().anyMatch(CourseResponse.CourseDetailResponse::isErrorYn))
                .smName(sm.getSmName())
                .smPhoneNumber(sm.getUsers().getPhoneNumber())
                .tonCode(vehicleDetail.getVehicleCode())
                .ton(vehicleDetail.getVehicleTon())
                .orderNum(response.getResultStopoverList().size())
                .mileage((int) response.getTotalDistance() / 1000)
                .totalTime(response.getTotalTime())
                .floorAreaRatio(floorAreaRatio)
                .courseDetailResponseList(courseDetailResponseList)
                .coordinatesResponseList(coordinatesResponseList)
                .build();
    }

    private boolean checkRestrictedTonCode(String vehicleCode, String restrictedTonCode) {
        if (restrictedTonCode == null || restrictedTonCode.isEmpty()) {
            return false;
        }
        return Arrays.stream(restrictedTonCode.split(","))
                .anyMatch(code -> vehicleCode.equals(code.trim()));
    }
}
