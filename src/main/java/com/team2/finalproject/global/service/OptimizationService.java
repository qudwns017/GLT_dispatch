package com.team2.finalproject.global.service;

import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.dto.response.CourseResponse;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.sm.model.type.ContractType;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.exception.TransportOrderErrorCode;
import com.team2.finalproject.domain.transportorder.exception.TransportOrderException;
import com.team2.finalproject.domain.transportorder.model.dto.request.OrderRequest;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicle.model.type.VehicleType;
import com.team2.finalproject.domain.vehicle.repository.VehicleRepository;
import com.team2.finalproject.global.util.TransportOrderUtil;
import com.team2.finalproject.global.util.request.OptimizationRequest;
import com.team2.finalproject.global.util.response.OptimizationResponse;
import com.team2.finalproject.global.util.response.ResultStopover;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class OptimizationService {

    private final DeliveryDestinationRepository deliveryDestinationRepository;
    private final SmRepository smRepository;
    private final VehicleRepository vehicleRepository;
    private final WebClient webClient;

    public OptimizationService(DeliveryDestinationRepository deliveryDestinationRepository,
                               SmRepository smRepository, VehicleRepository vehicleRepository,
                               @Value("${optimization-api.uri}") String uri) {
        this.deliveryDestinationRepository = deliveryDestinationRepository;
        this.smRepository = smRepository;
        this.vehicleRepository = vehicleRepository;
        this.webClient = WebClient.builder().baseUrl(uri).build();
    }

    public List<CourseResponse> callOptimizationApi(List<OptimizationRequest> optimizationRequests,
                                                    Map<Long, Map<String, List<OrderRequest>>> mapOrderAndAddressBySmId,
                                                    Map<String, String[]> addressMapping) {
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
            throw new TransportOrderException(TransportOrderErrorCode.FAILED_OPTIMIZE_ROUTE);
        }

        List<Long> keys = new ArrayList<>(mapOrderAndAddressBySmId.keySet());
        List<CourseResponse> courses = new ArrayList<>();
        for (int i = 0; i < responses.size(); i++) {
            // 하나의 경로 응답 생성
            Long key = keys.get(i);
            CourseResponse courseResponse =
                    createCourseResponse(mapOrderAndAddressBySmId.get(key), responses.get(i), addressMapping);
            courses.add(courseResponse);
        }

        return courses;
    }

    // 하나의 경로에 대한 작업
    private CourseResponse createCourseResponse(Map<String, List<OrderRequest>> mapOrderAndAddress,
                                                OptimizationResponse response, Map<String, String[]> addressMapping) {
        Sm sm = smRepository.findByIdOrThrow(
                mapOrderAndAddress.get(mapOrderAndAddress.keySet().iterator().next()).get(0).smId());
        Vehicle vehicle = vehicleRepository.findBySm(sm);

        CourseDetailResult courseDetailResult =
                createCourseDetailResponseList(sm, response.getResultStopoverList(), mapOrderAndAddress, vehicle,
                        addressMapping);

        int floorAreaRatio = calculateFloorAreaRatio(vehicle, courseDetailResult.courseDetailResponseList);
        List<CourseResponse.CoordinatesResponse> coordinatesResponseList = mapCoordinates(response);

        return buildCourseResponse(sm, vehicle, response, floorAreaRatio, courseDetailResult, coordinatesResponseList);
    }

    private CourseDetailResult createCourseDetailResponseList(Sm sm, List<ResultStopover> stopovers,
                                                              Map<String, List<OrderRequest>> orderRequestMap,
                                                              Vehicle vehicle,
                                                              Map<String, String[]> addressMapping) {
        List<CourseResponse.CourseDetailResponse> courseDetailResponseList = new ArrayList<>();
        int orderOrDistanceNum = 0;

        for (ResultStopover stopover : stopovers) {
            // 앞서 만든 특정 기사의 (주소, 주문) 맵에서 도로명 주소가 매칭되는 주문 불러오기
            OrderRequest matchingOrder = findMatchingOrder(orderRequestMap, stopover.getAddress());
            DeliveryDestination destination = findDestination(stopover);

            // 배송처(경유지)별로 진입 불가 톤코드를 검사
            boolean isRestricted = checkRestrictedTonCodes(vehicle, destination);

            boolean isDelayed = checkDelayedTime(
                    TransportOrderUtil.addDelayTime(stopover.getEndTime(), stopover.getDelayTime()),
                    matchingOrder.serviceRequestTime(), matchingOrder.serviceRequestDate());

            boolean isOverContractNum = checkOverContractNum(sm, stopover, orderOrDistanceNum);

            orderOrDistanceNum = updateContractNum(sm, stopover, orderOrDistanceNum);

            courseDetailResponseList.add(
                    createCourseDetailResponse(stopover, matchingOrder, destination, isRestricted, isDelayed,
                            isOverContractNum, addressMapping));
        }

        return new CourseDetailResult(courseDetailResponseList, orderOrDistanceNum);
    }

    public record CourseDetailResult(List<CourseResponse.CourseDetailResponse> courseDetailResponseList,
                                     int updatedContractNum) {

    }

    private boolean checkOverContractNum(Sm sm, ResultStopover stopover, int contractNum) {
        if (sm.getContractType() == ContractType.JIIP) {
            double totalDistance = contractNum + stopover.getDistance() / 1000.0;
            return totalDistance > sm.getContractNumOfMonth();
        } else if (sm.getContractType() == ContractType.DELIVERY) {
            int completedOrders = contractNum + 1;
            return completedOrders > sm.getContractNumOfMonth();
        }
        return false;
    }

    private int updateContractNum(Sm sm, ResultStopover stopover, int orderOrDistanceNum) {
        if (sm.getContractType() == ContractType.JIIP) {
            return orderOrDistanceNum + (int) (stopover.getDistance() / 1000.0);
        } else if (sm.getContractType() == ContractType.DELIVERY) {
            return orderOrDistanceNum + 1;
        }
        return orderOrDistanceNum;
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
                                                                           boolean isDelayed,
                                                                           boolean isOverContractNum,
                                                                           Map<String, String[]> addressMapping) {
        return CourseResponse.CourseDetailResponse.builder()
                .restrictedTonCode(isRestricted)
                .delayRequestTime(isDelayed)
                .overContractNum(isOverContractNum)
                .ett(stopover.getTimeFromPrevious() / 1000 / 60)
                .expectationOperationStartTime(stopover.getEndTime())
                .expectationOperationEndTime(
                        TransportOrderUtil.addDelayTime(stopover.getEndTime(), stopover.getDelayTime()))
                .lat(stopover.getLat())
                .lon(stopover.getLon())
                .distance(stopover.getDistance() / 1000.0)
                .roadAddress(stopover.getAddress().replace(addressMapping.get(stopover.getAddress())[1], "")
                        .substring(0,
                                stopover.getAddress().replace(addressMapping.get(stopover.getAddress())[1], "").length()
                                        - 1))
                .lotNumberAddress(addressMapping.get(stopover.getAddress())[0])
                .detailAddress(addressMapping.get(stopover.getAddress())[1])
                .expectedServiceDuration(TransportOrderUtil.convertLocalTimeToMinutes(stopover.getDelayTime()))
                .deliveryDestinationId(destination != null ? destination.getId() : 0)
                .managerName(destination != null ? destination.getManagerName() : null)
                .phoneNumber(destination != null ? destination.getPhoneNumber() : null)
                .deliveryType(order.deliveryType())
                .smId(order.smId())
                .smName(order.smName())
                .shipmentNumber(order.shipmentNumber())
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

    private int calculateFloorAreaRatio(Vehicle vehicle,
                                        List<CourseResponse.CourseDetailResponse> courseDetailResponseList) {
        double totalWeight = courseDetailResponseList.stream()
                .mapToDouble(CourseResponse.CourseDetailResponse::getWeight).sum();
        double totalVolume = courseDetailResponseList.stream()
                .mapToDouble(CourseResponse.CourseDetailResponse::getVolume).sum();

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

    private CourseResponse buildCourseResponse(Sm sm, Vehicle vehicle, OptimizationResponse response,
                                               int floorAreaRatio,
                                               CourseDetailResult courseDetailResult,
                                               List<CourseResponse.CoordinatesResponse> coordinatesResponseList) {

        boolean errorYn = courseDetailResult.courseDetailResponseList.stream().anyMatch(
                detail -> detail.isRestrictedTonCode() || detail.isDelayRequestTime() || detail.isOverContractNum());

        return CourseResponse.builder()
                .totalOrderOrDistanceNum(courseDetailResult.updatedContractNum)
                .availableNum(sm.getContractNumOfMonth() - sm.getCompletedNumOfMonth())
                .errorYn(errorYn)
                .smId(sm.getId())
                .smName(sm.getSmName())
                .smPhoneNumber(sm.getUsers().getPhoneNumber())
                .vehicleType(vehicle.getVehicleType().toString())
                .vehicleTon(vehicle.getVehicleTon())
                .orderNum(response.getResultStopoverList().size())
                .mileage((int) response.getTotalDistance() / 1000)
                .totalTime(response.getTotalTime() / 1000 / 60)
                .floorAreaRatio(floorAreaRatio)
                .breakStartTime(response.getBreakStartTime())
                .breakEndTime(response.getBreakEndTime())
                .restingPosition(response.getRestingPosition())
                .courseDetailResponseList(courseDetailResult.courseDetailResponseList)
                .coordinatesResponseList(coordinatesResponseList)
                .build();
    }

    private boolean checkRestrictedTonCodes(Vehicle vehicle, DeliveryDestination destination) {
        if (destination == null) {
            return false;
        }

        String restrictedTonCode = null;

        if (vehicle.getVehicleType() == VehicleType.WING_BODY) {
            restrictedTonCode = destination.getRestrictedWingBody();
        } else if (vehicle.getVehicleType() == VehicleType.BOX) {
            restrictedTonCode = destination.getRestrictedBox();
        } else if (vehicle.getVehicleType() == VehicleType.CARGO) {
            restrictedTonCode = destination.getRestrictedCargo();
        }

        return checkRestrictedTonCode(vehicle.getVehicleTon(), restrictedTonCode);
    }

    private boolean checkRestrictedTonCode(Double vehicleTon, String restrictedTonCode) {
        if (restrictedTonCode == null || restrictedTonCode.isEmpty()) {
            return false;
        }
        return Arrays.stream(restrictedTonCode.split(","))
                .map(String::trim)
                .mapToDouble(Double::parseDouble)
                .anyMatch(vehicleTon::equals);
    }

    private boolean checkDelayedTime(LocalDateTime expectationOperationEndTime, LocalTime serviceRequestTime,
                                     LocalDate serviceRequestDate) {
        if (serviceRequestTime == null || serviceRequestDate == null) {
            return false;
        }

        LocalDate endDate = expectationOperationEndTime.toLocalDate();
        LocalTime endTime = expectationOperationEndTime.toLocalTime();

        if (serviceRequestDate.isBefore(endDate)) {
            return true;
        }

        if (serviceRequestDate.isAfter(endDate)) {
            return false;
        }

        return endTime.isAfter(serviceRequestTime);
    }

}
