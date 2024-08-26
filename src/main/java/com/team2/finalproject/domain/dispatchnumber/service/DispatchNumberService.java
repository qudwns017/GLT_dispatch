package com.team2.finalproject.domain.dispatchnumber.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse.DispatchResponse;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchNumberService {

    private final DispatchNumberRepository dispatchNumberRepository;

    private final DeliveryDestinationRepository deliveryDestinationRepository;

    public DispatchListResponse getDispatchList(Long dispatchCodeId){

        DispatchNumber dispatchNumber = dispatchNumberRepository.findByIdWithJoinOrThrow(dispatchCodeId);

        List<Dispatch> dispatchList = dispatchNumber.getDispatchList();

        int totalCompletedOrderCount = dispatchList.stream()
            .mapToInt(Dispatch::getCompletedOrderCount) // Dispatch 객체의 getCompletedOrderCount 값을 int로 변환
            .sum(); // 모든 값을 합산
        int totalDeliveryOrderCount = dispatchList.stream()
            .mapToInt(Dispatch::getDeliveryOrderCount)
            .sum();
        double totalProgressionRate = (double) totalDeliveryOrderCount / totalCompletedOrderCount * 100;

        Center center = dispatchNumber.getCenter();
        DispatchListResponse.StartStopover startStopover = DispatchListResponse.StartStopover.of(center.getId(),center.getAddress(),center.getLatitude(),center.getLongitude(), center.getDelayTime());

        List<DispatchListResponse.Issue> issueList = new ArrayList<>();

        List<DispatchListResponse.DispatchResponse> dispatchResponseList = dispatchList.stream()
            .map((dispatch) -> {
                double progressionRate = (double) dispatch.getDeliveryOrderCount() / dispatch.getCompletedOrderCount() * 100;

                List<Map<String, Double>> stopoverList = createStopoverList(dispatch);
                List<Map<String, Double>> coordinates = createCoordinateList(dispatch);

                issueList.addAll(getIssueListOfDispatch(dispatch, dispatchCodeId));

                return DispatchResponse.of(dispatch.getId(), dispatch.getDeliveryStatus().getDescription(), dispatch.getSmName(), dispatch.getCompletedOrderCount(), dispatch.getDeliveryOrderCount(), (int) progressionRate,stopoverList,coordinates );
            }).toList();

        return DispatchListResponse.of(dispatchNumber.getDispatchNumber(), dispatchNumber.getDispatchName(),(int) totalProgressionRate,totalCompletedOrderCount,totalDeliveryOrderCount,issueList.size(),startStopover,dispatchResponseList,issueList);
    }

    private List<Map<String, Double>> createStopoverList(Dispatch dispatch) {
        return dispatch.getDispatchDetailList().stream()
            .map(detail -> {
                Map<String, Double> stopover = new HashMap<>();
                stopover.put("lat", detail.getDestinationLatitude());
                stopover.put("lon", detail.getDestinationLongitude());
                return stopover;
            }).toList();
    }

    private List<Map<String, Double>> createCoordinateList(Dispatch dispatch) {
        return Arrays.stream(dispatch.getPath().getCoordinates())
            .map(coord -> {
                Map<String, Double> coordinate = new HashMap<>();
                coordinate.put("lat", coord.getX());
                coordinate.put("lon", coord.getY());
                return coordinate;
            }).toList();
    }

    private List<DispatchListResponse.Issue> getIssueListOfDispatch(Dispatch dispatch, Long dispatchCodeId) {
        List<DispatchListResponse.Issue> issueList = new ArrayList<>();

        for (DispatchDetail dispatchDetail : dispatch.getDispatchDetailList()) {
            if (dispatchDetail.getDispatchDetailStatus() == DispatchDetailStatus.DELIVERY_DELAY) {
                String comment = null;
                if (dispatchDetail.getDestinationId() != null) {
                    comment = deliveryDestinationRepository.findByIdOrThrow(dispatchDetail.getDestinationId()).getComment();
                }
                DispatchListResponse.Issue issue = DispatchListResponse.Issue.of(
                    dispatchCodeId,
                    dispatch.getId(),
                    dispatch.getSmName(),
                    dispatchDetail.getTransportOrder().getCustomerAddress(),
                    dispatchDetail.getDestinationId(),
                    comment
                );
                issueList.add(issue);
            }
        }
        return issueList;
    }
}
