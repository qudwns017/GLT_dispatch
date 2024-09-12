package com.team2.finalproject.domain.dispatchdetail.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchdetail.exception.DispatchDetailErrorCode;
import com.team2.finalproject.domain.dispatchdetail.exception.DispatchDetailException;
import com.team2.finalproject.domain.dispatchdetail.model.dto.response.DispatchDetailResponse;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.model.type.DestinationType;
import com.team2.finalproject.domain.dispatchdetail.repository.DispatchDetailRepository;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DispatchDetailService {

    private final DispatchDetailRepository dispatchDetailRepository;

    private final DispatchNumberRepository dispatchNumberRepository;

    private final DispatchRepository dispatchRepository;
    private final CenterRepository centerRepository;
    private final DeliveryDestinationRepository deliveryDestinationRepository;

    @Transactional(readOnly = true)
    public DispatchDetailResponse getDispatchDetail(Long dispatchId) {
        Dispatch dispatch = dispatchRepository.findByIdWithDetailsOrThrow(dispatchId);
        log.info("정보를 가져올 dispatchId: {}", dispatchId);

        Sm sm = dispatch.getSm();
        Users users = sm.getUsers();
        Vehicle vehicle = sm.getVehicle();

        List<DispatchDetail> dispatchDetails = dispatch.getDispatchDetailList();
        log.info("배차 상세 개수: {}", dispatchDetails.size());
        log.info("despatchDetailIds: {}", dispatchDetails.stream().map(DispatchDetail::getId).toList());

        // startStopover
        Center center = centerRepository.findByCenterCodeOrThrow(dispatch.getDeparturePlaceCode());
        DispatchDetailResponse.StartStopover startStopover = DispatchDetailResponse.getStartStopover(center, dispatch.getDepartureTime());


        // dispatchDetailList
        List<DispatchDetailResponse.DispatchDetail> dispatchDetailList = dispatchDetails.stream()
                .map(dispatchDetail -> {
                    TransportOrder transportOrder = dispatchDetail.getTransportOrder();
                    return DispatchDetailResponse.getDispatchDetail(
                            dispatchDetail, transportOrder, getComment(dispatchDetail));
                })
                .toList();

        return DispatchDetailResponse.of(
                dispatch, sm, users, vehicle, startStopover, dispatchDetailList);
    }

    private String getComment(DispatchDetail dispatchDetail) {
        if (dispatchDetail.getDestinationType() == DestinationType.CENTER) {
            return centerRepository.findCommentByIdOrNull(dispatchDetail.getDestinationId());
        }else if(dispatchDetail.getDestinationType() == DestinationType.DELIVERY_DESTINATION) {
            return deliveryDestinationRepository.findCommentByIdOrNull(dispatchDetail.getDestinationId());
        }
        return null;
    }

    public void cancelDispatchDetailList(List<Long> dispatchDetailIdList, Long centerId){
        List<DispatchDetail> dispatchDetailList = dispatchDetailRepository.findWithTransportOrderAndCenterByIdIn(dispatchDetailIdList);

        if(dispatchDetailList.size() != dispatchDetailIdList.size()){
            throw new DispatchDetailException(DispatchDetailErrorCode.INVALID_IN_REQUEST);
        }

        validateDispatchDetailList(dispatchDetailList,centerId);

        dispatchDetailList.forEach(
            dispatchDetail -> {
                dispatchDetail.cancel();
                dispatchDetail.getTransportOrder().pend();
                dispatchDetailRepository.save(dispatchDetail);
            }
        );

        Dispatch dispatch = dispatchDetailList.get(0).getDispatch();
        dispatch.minusOrderCount(dispatchDetailList.size());

        if(dispatch.getDeliveryOrderCount()-dispatch.getCompletedOrderCount() <= 0){
            dispatch.complete();
        }
        dispatchRepository.save(dispatch);

        DispatchNumber dispatchNumber = dispatch.getDispatchNumber();
        boolean allCompleted = dispatchNumber.getDispatchList().stream()
            .allMatch(dispatchNumberDispatch -> dispatchNumberDispatch.getDeliveryStatus() == DispatchStatus.TRANSPORTATION_COMPLETED);
        if(allCompleted){
            dispatchNumber.complete();
            dispatchNumberRepository.save(dispatchNumber);
        }

    }

    private void validateDispatchDetailList(List<DispatchDetail> dispatchDetailList, Long centerId) {
        // 모든 DispatchDetail 검증
        boolean allMatch = dispatchDetailList.stream()
            .allMatch(dispatchDetail -> {
                TransportOrder transportOrder = dispatchDetail.getTransportOrder();
                if (transportOrder == null) {
                    throw new DispatchDetailException(DispatchDetailErrorCode.NOT_FOUND_TRANSPORT_ORDER_IN_DISPATCH_DETAIL);
                }
                Center center = transportOrder.getCenter();
                return center != null && center.getId().equals(centerId);
            });

        if (!allMatch) {
            throw new DispatchDetailException(DispatchDetailErrorCode.NOT_MATCH_CENTER_AND_DISPATCH_DETAIL);
        }
    }
}
