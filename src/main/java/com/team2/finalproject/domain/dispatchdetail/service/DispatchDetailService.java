package com.team2.finalproject.domain.dispatchdetail.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.repository.DispatchRepository;
import com.team2.finalproject.domain.dispatchdetail.model.dto.response.DispatchDetailResponse;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.model.type.DestinationType;
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

        // startStopover
        Center center = centerRepository.findByCenterCodeOrThrow(dispatch.getDeparturePlaceCode());
        DispatchDetailResponse.StartStopover startStopover = DispatchDetailResponse.getStartStopover(center);

        // dispatchDetailList
        List<DispatchDetailResponse.DispatchDetail> dispatchDetailList = dispatchDetails.stream()
                .map(dispatchDetail -> {
                    TransportOrder transportOrder = dispatchDetail.getTransportOrder();
                    return DispatchDetailResponse.getDispatchDetail(
                            dispatchDetail, transportOrder, getComment(dispatchDetail));
                })
                .toList();

        return DispatchDetailResponse.of(
                dispatch, sm, users, vehicle, vehicleDetail, startStopover, dispatchDetailList);
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
