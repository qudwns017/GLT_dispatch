package com.team2.finalproject.domain.dispatchdetail.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchdetail.model.dto.response.DispatchDetailResponse;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.model.type.DestinationType;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicledetail.model.entity.VehicleDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DispatchDetailService {

    private final DispatchRepository dispatchRepository;
    private final CenterRepository centerRepository;
    private final DeliveryDestinationRepository deliveryDestinationRepository;

    @Transactional(readOnly = true)
    public DispatchDetailResponse getDispatchDetail(Long dispatchId) {
        Dispatch dispatch = dispatchRepository.findByIdWithDetailsOrThrow(dispatchId);

        Sm sm = dispatch.getSm();
        Users users = sm.getUsers();
        Vehicle vehicle = sm.getVehicle();
        VehicleDetail vehicleDetail = vehicle.getVehicleDetail();

        List<DispatchDetail> dispatchDetails = dispatch.getDispatchDetailList();

        Center center = centerRepository.findByCenterCodeOrThrow(dispatch.getDeparturePlaceCode());
        DispatchDetailResponse.StartStopover startStopover = DispatchDetailResponse.of(center);

        List<DispatchDetailResponse.DispatchDetail> dispatchDetailList = dispatchDetails.stream()
                .map(this::getDispatchDetailList)
                .toList();

        return DispatchDetailResponse.builder()
                .smName(sm.getSmName())
                .smPhoneNumber(users.getPhoneNumber())
                .floorAreaRatio(dispatch.getLoadingRate())
                .vehicleType(vehicle.getVehicleType())
                .vehicleTon(vehicleDetail.getVehicleTon())
                .progressionRate(calcProgress(dispatch.getDeliveryOrderCount(), dispatch.getCompletedOrderCount()))
                .completedOrderCount(dispatch.getCompletedOrderCount())
                .deliveryOrderCount(dispatch.getDeliveryOrderCount())
                .totalTime(dispatch.getTotalTime())
                .issue(dispatch.getIssue())
                .startStopover(startStopover)
                .dispatchDetailList(dispatchDetailList)
                .build();
    }

    // 진행률 계산
    private int calcProgress(int totalOrder, int completedOrder) {
        if (totalOrder == 0) {
            return 0;
        }
        return (int) Math.round((double) completedOrder / totalOrder * 100);
    }

    private DispatchDetailResponse.DispatchDetail getDispatchDetailList(DispatchDetail dispatchDetail) {

        TransportOrder transportOrder = dispatchDetail.getTransportOrder();

        return DispatchDetailResponse.DispatchDetail.builder()
                .dispatchDetailId(dispatchDetail.getId())
                .dispatchDetailStatus(getDispatchDetailStatus(dispatchDetail))
                .operationStartTime(dispatchDetail.getOperationStartTime())
                .operationEndTime(dispatchDetail.getOperationEndTime())
                .expectationOperationStartTime(dispatchDetail.getExpectationOperationStartTime())
                .expectationOperationEndTime(dispatchDetail.getExpectationOperationEndTime())
                .ett(dispatchDetail.getEtt())
                .destinationType(dispatchDetail.getDestinationType())
                .destinationId(dispatchDetail.getDestinationId())
                .destinationComment(getComment(dispatchDetail))
                .address(transportOrder.getCustomerAddress())
                .transportOrderId(transportOrder.getId())
                .lat(dispatchDetail.getDestinationLatitude())
                .lon(dispatchDetail.getDestinationLongitude())
                .build();
    }

    private DispatchDetailStatus getDispatchDetailStatus(DispatchDetail dispatchDetail) {
        if(dispatchDetail.isResting()) {
            return DispatchDetailStatus.RESTING;
        }else {
            return dispatchDetail.getDispatchDetailStatus();
        }
    }

    private String getComment(DispatchDetail dispatchDetail) {
        if (dispatchDetail.getDestinationType() == DestinationType.CENTER) {
            return centerRepository.findCommentByIdOrThrow(dispatchDetail.getDestinationId());
        }else if(dispatchDetail.getDestinationType() == DestinationType.DELIVERY_DESTINATION) {
            return deliveryDestinationRepository.findCommentByIdOrThrow(dispatchDetail.getDestinationId());
        }
        return null;
    }
}
