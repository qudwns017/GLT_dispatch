package com.team2.finalproject.domain.dispatch.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchConfirmRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchConfirmRequest.DispatchList;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchConfirmRequest.DispatchList.DispatchDetailList;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchUpdateRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchUpdateRequest.Order;
import com.team2.finalproject.domain.dispatch.model.dto.request.IssueRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchUpdateResponse;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.dispatchdetail.repository.DispatchDetailRepository;
import com.team2.finalproject.domain.dispatchnumber.exception.DispatchNumberErrorCode;
import com.team2.finalproject.domain.dispatchnumber.exception.DispatchNumberException;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.sm.model.type.ContractType;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.transportorder.repository.TransportOrderRepository;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicle.model.type.VehicleType;
import com.team2.finalproject.domain.vehicle.repository.VehicleRepository;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.util.optimization.OptimizationApiUtil;
import com.team2.finalproject.global.util.optimization.OptimizationRequest;
import com.team2.finalproject.global.util.optimization.OptimizationResponse;
import com.team2.finalproject.global.util.optimization.OptimizationResponse.ResultStopover;
import com.team2.finalproject.global.util.request.Stopover;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DispatchService {
    private final DispatchRepository dispatchRepository;
    private final DispatchNumberRepository dispatchNumberRepository;
    private final SmRepository smRepository;
    private final TransportOrderRepository transportOrderRepository;
    private final DispatchDetailRepository dispatchDetailRepository;

    private final OptimizationApiUtil optimizationApiUtil;
    private final CenterRepository centerRepository;

    private final DeliveryDestinationRepository deliveryDestinationRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional(readOnly = true)
    public DispatchUpdateResponse updateDispatch(DispatchUpdateRequest request, UserDetailsImpl userDetails) {
        List<DispatchUpdateRequest.Order> orders = request.orderList();

        Long centerId = userDetails.getUsers().getCenter().getId();
        Center center = centerRepository.findByIdOrThrow(centerId);
        Stopover startStopoverRequest = Stopover.of(center.getRoadAddress(),
                center.getLatitude(), center.getLongitude(),
                LocalTime.of((center.getDelayTime() / 60)+1, center.getDelayTime() % 60, 0));  // 상차 추가작업시간 + 상차기본시간 1시간

        List<Stopover> stopoverList = orders.stream()
                .map((order) -> Stopover.of(order.roadAddress(), order.lat(), order.lon(),
                        LocalTime.of(order.expectedServiceDuration() / 60, order.expectedServiceDuration() % 60, 0)))
                .toList();

        Sm sm = smRepository.findByIdOrThrow(request.smId());

        OptimizationResponse optimizationResponse = optimizationApiUtil.getOptimizationResponse(
                OptimizationRequest.of(request.loadingStartTime(), startStopoverRequest, stopoverList,
                        sm.getBreakStartTime(), sm.getBreakTime()));

        int availableNum = sm.getContractNumOfMonth() - sm.getCompletedNumOfMonth(); // 가용주문
        // 주문의 전체 주문 수 or 거리
        int totalOrderOrDistanceNum =
                sm.getContractType() == ContractType.JIIP ? (int) (optimizationResponse.totalDistance() / 1000)
                        : orders.size();

        DispatchUpdateResponse.StartStopover startStopover = DispatchUpdateResponse.StartStopover.of(
                optimizationResponse.startStopover().address(),
                optimizationResponse.startStopover().lat(),
                optimizationResponse.startStopover().lon(),
                optimizationResponse.startStopover().delayTime().getHour() * 60
                        + optimizationResponse.startStopover().delayTime().getMinute(),
                optimizationResponse.resultStopoverList().get(0).startTime()
        );

        List<OptimizationResponse.ResultStopover> resultStopoverList = optimizationResponse.resultStopoverList();
        List<DispatchUpdateResponse.DispatchDetailResponse> dispatchDetailResponseList = dispatchDetailResponseList(
                resultStopoverList, orders, request.loadingStartTime(), sm, availableNum);

        int floorAreaRatio;

        if (ContractType.DELIVERY.equals(sm.getContractType())) {
            double totalVolume = orders.stream()
                    .mapToDouble(order -> order.volume() != null ? order.volume() * order.productQuantity() : 0.0)
                    .sum();
            floorAreaRatio = (int) ((totalVolume / sm.getVehicle().getMaxLoadVolume()) * 100);
        } else {
            double totalWeight = orders.stream()
                    .mapToDouble(order -> order.weight() != null ? order.weight() * order.productQuantity() : 0.0)
                    .sum();
            floorAreaRatio = (int) ((totalWeight / sm.getVehicle().getMaxLoadWeight()) * 100);
            System.out.println("totalWeight: " + totalWeight + " maxLoadWeight: " + sm.getVehicle().getMaxLoadWeight());
        }

        return DispatchUpdateResponse.of(optimizationResponse.totalDistance() / 1000, optimizationResponse.totalTime(),
                optimizationResponse.breakStartTime(), optimizationResponse.breakEndTime(),
                optimizationResponse.restingPosition(), totalOrderOrDistanceNum,
                calculateTotalFloorAreaRatio(request.totalWeight(), request.totalVolume(), sm.getContractType(),
                        request.smIdList()),
                floorAreaRatio, availableNum,
                sm.getContractType().getContractType(), startStopover, dispatchDetailResponseList,
                optimizationResponse.coordinates());
    }

    private int calculateTotalFloorAreaRatio(double totalWeight, double totalVolume, ContractType contractType,
                                             List<Long> smIds) {
        if (ContractType.JIIP.equals(contractType)) {
            return (int) ((totalWeight / vehicleRepository.findTotalMaxLoadWeightBySmIds(smIds)) * 100);
        } else if (ContractType.DELIVERY.equals(contractType)) {
            return (int) ((totalVolume / vehicleRepository.findTotalMaxLoadVolumeBySmIds(smIds)) * 100);
        }
        return 0;
    }

    @Transactional
    public void confirmDispatch(DispatchConfirmRequest request, UserDetailsImpl userDetails) {
        List<Dispatch> updateDispatchList = new ArrayList<>();
        List<DispatchDetail> pendingDispatchDetailList = new ArrayList<>();

        Users usersEntity = userDetails.getUsers();
        Center centerEntity = userDetails.getCenter();
        DispatchNumber dispatchNumberEntity = DispatchConfirmRequest.toDispatchNumberEntity(request, usersEntity,
                centerEntity);
        DispatchNumber dispatchNumber = dispatchNumberRepository.save(dispatchNumberEntity);

        for (DispatchList dispatch : request.dispatchList()) {
            double totalVolume = dispatch.dispatchDetailList().stream().mapToDouble((dispatchDetailList -> dispatchDetailList.volume() * dispatchDetailList.productQuantity())).sum();
            double totalWeight = dispatch.dispatchDetailList().stream().mapToDouble((dispatchDetailList -> dispatchDetailList.weight() * dispatchDetailList.productQuantity())).sum();
            double totalDistance = dispatch.dispatchDetailList().stream().mapToDouble((DispatchDetailList::distance)).sum();
            int totalTime = dispatch.dispatchDetailList().stream().mapToInt((DispatchDetailList::ett)).sum();

            Sm smEntity = smRepository.findByIdOrThrow(dispatch.smId());

            GeometryFactory geometryFactory = new GeometryFactory();
            List<Coordinate> coordinates = new ArrayList<>();

            for (com.team2.finalproject.global.util.response.Coordinate coordinate : dispatch.coordinates()) {
                coordinates.add(new Coordinate(coordinate.getLat(), coordinate.getLon()));
            }

            LineString path = geometryFactory.createLineString(coordinates.toArray(new Coordinate[0]));

            Dispatch dispatchEntity = DispatchConfirmRequest.toDispatchEntity(
                    request, dispatchNumber, smEntity, centerEntity,
                    totalVolume, totalWeight, totalDistance, totalTime, path, dispatch
            );

            Dispatch savedDispatch = dispatchRepository.save(dispatchEntity);

            for (DispatchDetailList dispatchDetail : dispatch.dispatchDetailList()) {
                TransportOrder savedTransportOrderEntity = transportOrderRepository.save(
                        DispatchConfirmRequest.toTransportOrderEntity(
                                dispatchDetail, centerEntity
                        ));

                pendingDispatchDetailList.add(DispatchConfirmRequest.toDispatchDetailEntity(
                        dispatchDetail, savedDispatch, savedTransportOrderEntity
                ));
            }
            updateDispatchList.add(savedDispatch);
        }

        dispatchRepository.saveAll(updateDispatchList);
        dispatchDetailRepository.saveAll(pendingDispatchDetailList);
    }

    private List<DispatchUpdateResponse.DispatchDetailResponse> dispatchDetailResponseList(
            List<OptimizationResponse.ResultStopover> resultStopoverList,
            List<DispatchUpdateRequest.Order> orderList,
            LocalDateTime startDateTime,
            Sm sm,
            int availableNum
    ) {

        List<DispatchUpdateResponse.DispatchDetailResponse> dispatchDetailResponseList = new ArrayList<>();

        int totalOrderOrDistanceNum = 0;
        Vehicle vehicle = sm.getVehicle();
        boolean overFloorAreaRatio = false;
        double totalVolumeOrWeight = 0.0;

        for (int i = 0; i < resultStopoverList.size(); i++) {

            boolean delayRequestTime = false;

            ResultStopover resultStopover = resultStopoverList.get(i);
            Order order = orderList.get(i);

            // 희망 도착시간 및 도착일 초과 확인
            if (order.serviceRequestDate().isBefore(startDateTime.toLocalDate()) ||
                    (order.serviceRequestTime() != null &&
                            order.serviceRequestTime().isBefore(resultStopover.endTime().toLocalTime()) &&
                            order.serviceRequestDate().isEqual(startDateTime.toLocalDate()))) {
                delayRequestTime = true;
            }

            // 배송처 오류 확인
            DeliveryDestination destination = deliveryDestinationRepository.findByFullAddress(order.roadAddress(),
                    order.detailAddress());

            boolean isEntryRestricted = checkRestrictedTonCodes(vehicle, destination);

            boolean overContractNum = false;
            totalOrderOrDistanceNum +=
                    sm.getContractType() == ContractType.JIIP ? (int) (resultStopoverList.get(i).distance() / 1000) : 1;
            if (totalOrderOrDistanceNum > availableNum) {
                overContractNum = true;
            }

            int floorAreaRatio;
            if (ContractType.DELIVERY.equals(sm.getContractType())) {
                totalVolumeOrWeight += order.volume();
                floorAreaRatio = (int) (totalVolumeOrWeight / vehicle.getMaxLoadVolume() * 100);
            } else {
                totalVolumeOrWeight += order.weight();
                floorAreaRatio = (int) (totalVolumeOrWeight / vehicle.getMaxLoadWeight() * 100);
            }

            if (floorAreaRatio > 100) {
                overFloorAreaRatio = true;
            }

            DispatchUpdateResponse.DispatchDetailResponse dispatchDetailResponse = DispatchUpdateResponse.DispatchDetailResponse.of(
                    resultStopover.address(),
                    resultStopover.timeFromPrevious() / 60000, // ms -> 분
                    resultStopover.endTime(),
                    i + 1 != resultStopoverList.size() ? resultStopoverList.get(i + 1).startTime()
                            : resultStopover.endTime()
                                    .plusHours(resultStopover.delayTime().getHour())
                                    .plusMinutes(resultStopover.delayTime().getMinute())
                                    .plusSeconds(resultStopover.delayTime().getSecond()),
                    resultStopover.delayTime().getHour() * 60
                            + resultStopover.delayTime().getMinute(),
                    resultStopover.lat(),
                    resultStopover.lon(),
                    resultStopover.distance(),
                    delayRequestTime,
                    isEntryRestricted,
                    overContractNum,
                    overFloorAreaRatio
            );
            dispatchDetailResponseList.add(dispatchDetailResponse);
        }
        return dispatchDetailResponseList;
    }

    //TODO 유틸로 빼기
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

    //TODO 유틸로 빼기
    private boolean checkRestrictedTonCode(Double vehicleTon, String restrictedTonCode) {
        if (restrictedTonCode == null || restrictedTonCode.isEmpty()) {
            return false;
        }
        return Arrays.stream(restrictedTonCode.split(","))
                .map(String::trim)
                .mapToDouble(Double::parseDouble)
                .anyMatch(vehicleTon::equals);
    }

    // 배차 탭에서의 배차 취소
    @Transactional
    public void cancelDispatch(DispatchCancelRequest request, Center center) {
        // center, ids로 DispatchNumber 리스트 가져오기
        List<DispatchNumber> dispatchNumbers =
                dispatchNumberRepository.findByIdsAndCenter(request.dispatchNumberIds(), center);

        // 입력 받은 DispatchNumberIds의 개수와 dispatchNumbers의 개수가 다를 경우 잘못된 요청
        // 존재 하지 않거나 담당 센터가 아닌 배차에 대한 취소 요청
        if (dispatchNumbers.size() != request.dispatchNumberIds().size()) {
            throw new DispatchNumberException(DispatchNumberErrorCode.INVALID_IN_REQUEST);
        }

        // 주행 완료된 DispatchNumber가 포함되어 있는지 확인
        boolean hasCompletedDispatch = dispatchNumbers.stream()
                .anyMatch(dispatchNumber -> dispatchNumber.getStatus() == DispatchNumberStatus.COMPLETED);

        if (hasCompletedDispatch) {
            throw new DispatchNumberException(DispatchNumberErrorCode.CANNOT_CANCEL_COMPLETED_DISPATCH_NUMBER);
        }

        // DispatchNumber 리스트를 상태에 따라 분리
        Map<Boolean, List<DispatchNumber>> partitionedDispatchNumbers = dispatchNumbers.stream()
                .collect(Collectors.partitioningBy(
                        dispatchNumber -> dispatchNumber.getStatus() == DispatchNumberStatus.IN_TRANSIT));

        List<DispatchNumber> inTransitDispatchNumbers = partitionedDispatchNumbers.get(true);
        List<DispatchNumber> waitingDispatchNumbers = partitionedDispatchNumbers.get(false);

        // 주행 중인 경우
        cancelInTransitDispatch(inTransitDispatchNumbers);

        // 주행 대기인 경우 -  해당하는 DispatchNumber, Dispatch, DispatchDetail, Transport_order 모두 삭제
        dispatchNumberRepository.deleteAll(waitingDispatchNumbers);
    }

    // 주행 중인 경우 배차 취소
    private void cancelInTransitDispatch(List<DispatchNumber> inTransitDispatchNumbers) {
        // DispatchNumber 상태 COMPLETED로 변경
        updateDispatchNumberStatusToCompleted(inTransitDispatchNumbers);

        // 각 Dispatch 처리 (내부의 DispatchDetail, TransportOrder 포함)
        inTransitDispatchNumbers.stream()
                .flatMap(dn -> dn.getDispatchList().stream())
                .forEach(this::processDispatchCancellation);
    }

    // DispatchNumber 상태 COMPLETED로 변경
    private void updateDispatchNumberStatusToCompleted(List<DispatchNumber> dispatchNumbers) {
        dispatchNumbers.forEach(dn -> {
            dn.setStatus(DispatchNumberStatus.COMPLETED);
            dispatchNumberRepository.save(dn);
        });
    }

    // Dispatch 취소 처리
    // Dispatch 상태 COMPLETED로 변경, 미 배송 상태 취소로 변경, 총 주문 수에서 취소 주문 수 빼기, 운송 주문 보류 처리
    private void processDispatchCancellation(Dispatch dispatch) {
        // Dispatch 상태 COMPLETED로 변경
        dispatch.setDeliveryStatus(DispatchStatus.TRANSPORTATION_COMPLETED);

        // 미 배송된 DispatchDetail 개수
        long undeliveredCount = dispatch.getDispatchDetailList().stream()
                .filter(dd -> dd.getDispatchDetailStatus() != DispatchDetailStatus.WORK_COMPLETED)
                .count();

        // 미 배송된 DispatchDetail의 상태를 CANCELED로 변경
        dispatch.getDispatchDetailList().stream()
                .filter(dd -> dd.getDispatchDetailStatus() != DispatchDetailStatus.WORK_COMPLETED)
                .forEach(dd -> {
                    dd.setDispatchDetailStatus(DispatchDetailStatus.CANCELED);
                    dispatchDetailRepository.save(dd); // DispatchDetail 업데이트

                    // TransportOrder isPending = true로 변경
                    TransportOrder transportOrder = dd.getTransportOrder();
                    if (transportOrder != null) {
                        transportOrder.setPending(true);
                        transportOrderRepository.save(transportOrder);
                    }
                });

        // Dispatch 총 주문수에서 미 배송된 수만큼 빼기
        dispatch.setDeliveryOrderCount(dispatch.getDeliveryOrderCount() - (int) undeliveredCount);

        // Dispatch 엔티티 업데이트
        dispatchRepository.save(dispatch);
    }

    public void updateIssue(long dispatchId, IssueRequest request) {
        Dispatch dispatch = dispatchRepository.findByIdOrThrow(dispatchId);
        dispatch.update(request);
        dispatchRepository.save(dispatch);
    }
}
