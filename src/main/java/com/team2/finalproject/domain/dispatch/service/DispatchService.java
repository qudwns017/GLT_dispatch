package com.team2.finalproject.domain.dispatch.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatch.exception.DispatchErrorCode;
import com.team2.finalproject.domain.dispatch.exception.DispatchException;
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
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.transportorder.repository.TransportOrderRepository;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.util.optimization.OptimizationApiUtil;
import com.team2.finalproject.global.util.optimization.OptimizationRequest;
import com.team2.finalproject.global.util.optimization.OptimizationResponse;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Transactional(readOnly = true)
    public DispatchUpdateResponse updateDispatch(DispatchUpdateRequest request) {
        List<DispatchUpdateRequest.Order> orders = request.orderList();

        Order startOrder = orders.get(0);
        orders.remove(0);

        OptimizationRequest.Stopover startStopoverRequest = OptimizationRequest.Stopover.of(startOrder.address(),
                startOrder.lat(), startOrder.lon(),
                LocalTime.of(startOrder.expectedServiceDuration() / 60, startOrder.expectedServiceDuration() % 60, 0));
        List<OptimizationRequest.Stopover> stopoverList = orders.stream()
                .map((order) -> OptimizationRequest.Stopover.of(order.address(), order.lat(), order.lon(),
                        LocalTime.of(order.expectedServiceDuration() / 60, order.expectedServiceDuration() % 60, 0)))
                .toList();

        OptimizationResponse optimizationResponse = optimizationApiUtil.getOptimizationResponse(
                OptimizationRequest.of(request.loadingStartTime(), startStopoverRequest, stopoverList));

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
                resultStopoverList);

        return DispatchUpdateResponse.of(optimizationResponse.totalDistance() / 1000, optimizationResponse.totalTime(),
                startStopover, dispatchDetailResponseList, optimizationResponse.coordinates());
    }

    @Transactional
    public void confirmDispatch(DispatchConfirmRequest request, UserDetailsImpl userDetails) {
        List<Dispatch> updateDispatchList = new ArrayList<>();
        List<DispatchDetail> pendingDispatchDetailList = new ArrayList<>();

        Users usersEntity = userDetails.getUsers();
        Center centerEntity = userDetails.getCenter();
        DispatchNumber dispatchNumberEntity = DispatchConfirmRequest.toDispatchNumberEntity(request, usersEntity,
                centerEntity);
        DispatchNumber savedDispatchNumber = dispatchNumberRepository.save(dispatchNumberEntity);

        for (DispatchList dispatch : request.dispatchList()) {
            double totalVolume = 0;
            double totalWeight = 0;
            double totalDistance = 0;
            int totalTime = 0;

            Sm smEntity = smRepository.findByIdOrThrow(dispatch.smId());

            GeometryFactory geometryFactory = new GeometryFactory();
            List<Coordinate> coordinates = new ArrayList<>();

            for (com.team2.finalproject.global.util.response.Coordinate coordinate : dispatch.coordinates()) {
                coordinates.add(new Coordinate(coordinate.getLat(), coordinate.getLon()));
            }

            LineString path = geometryFactory.createLineString(coordinates.toArray(new Coordinate[0]));

            Dispatch dispatchEntity = DispatchConfirmRequest.toDispatchEntity(
                    request, savedDispatchNumber, smEntity, centerEntity,
                    totalVolume, totalWeight, totalDistance, totalTime, path
            );

            Dispatch savedDispatch = dispatchRepository.save(dispatchEntity);

            for (DispatchDetailList dispatchDetail : dispatch.dispatchDetailList()) {
                TransportOrder savedTransportOrderEntity = transportOrderRepository.save(
                        DispatchConfirmRequest.toTransportOrderEntity(
                                dispatchDetail, centerEntity
                        ));

                totalVolume += dispatchDetail.volume();
                totalWeight += dispatchDetail.weight();
                totalDistance += dispatchDetail.distance();
                totalTime += dispatchDetail.ett();

                pendingDispatchDetailList.add(DispatchConfirmRequest.toDispatchDetailEntity(
                        dispatchDetail, savedDispatch, savedTransportOrderEntity
                ));
            }
            savedDispatch.update(totalVolume, totalWeight, totalDistance, totalTime);
            updateDispatchList.add(savedDispatch);
        }

        dispatchRepository.saveAll(updateDispatchList);
        dispatchDetailRepository.saveAll(pendingDispatchDetailList);
    }

    private List<DispatchUpdateResponse.DispatchDetailResponse> dispatchDetailResponseList(
            List<OptimizationResponse.ResultStopover> resultStopoverList) {

        List<DispatchUpdateResponse.DispatchDetailResponse> dispatchDetailResponseList = new ArrayList<>();

        for (int i = 0; i < resultStopoverList.size(); i++) {
            DispatchUpdateResponse.DispatchDetailResponse dispatchDetailResponse = DispatchUpdateResponse.DispatchDetailResponse.of(
                    resultStopoverList.get(i).address(),
                    resultStopoverList.get(i).timeFromPrevious() / 60000, // ms -> 분
                    resultStopoverList.get(i).endTime(),
                    i + 1 != resultStopoverList.size() ? resultStopoverList.get(i + 1).startTime()
                            : resultStopoverList.get(i).endTime()
                                    .plusHours(resultStopoverList.get(i).delayTime().getHour())
                                    .plusMinutes(resultStopoverList.get(i).delayTime().getMinute())
                                    .plusSeconds(resultStopoverList.get(i).delayTime().getSecond()),
                    resultStopoverList.get(i).delayTime().getHour() * 60
                            + resultStopoverList.get(i).delayTime().getMinute(),
                    resultStopoverList.get(i).lat(),
                    resultStopoverList.get(i).lon(),
                    resultStopoverList.get(i).distance()
            );
            dispatchDetailResponseList.add(dispatchDetailResponse);
        }
        return dispatchDetailResponseList;
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
            throw new DispatchException(DispatchErrorCode.INVALID_IN_REQUEST);
        }

        // DispatchNumber 리스트를 상태에 따라 분리
        Map<Boolean, List<DispatchNumber>> partitionedDispatchNumbers = dispatchNumbers.stream()
                .collect(Collectors.partitioningBy(dispatchNumber -> dispatchNumber.getStatus() == DispatchNumberStatus.IN_TRANSIT));

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
        dispatch.setDeliveryStatus(DispatchStatus.COMPLETED);

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
