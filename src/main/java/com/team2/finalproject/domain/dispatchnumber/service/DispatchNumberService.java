package com.team2.finalproject.domain.dispatchnumber.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse.DispatchResponse;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse.Issue;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.repository.DispatchNumberRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchNumberService {

    private final CenterRepository centerRepository;

    private final DispatchNumberRepository dispatchNumberRepository;

    private final DeliveryDestinationRepository deliveryDestinationRepository;

    public DispatchListResponse getDispatchList(Long dispatchCodeId){

        DispatchNumber dispatchNumber = dispatchNumberRepository.findByIdWithJoinAndThrow(dispatchCodeId);

        List<Dispatch> dispatchList = dispatchNumber.getDispatcheList();

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

                List<DispatchDetail> dispatchDetailList = dispatch.getDispatchDetailList();
                List<Map<String, Double>> stopoverList = new ArrayList<>();
                for(DispatchDetail dispatchDetail : dispatchDetailList){
                    Map<String, Double> stopover = new HashMap<>();
                    stopover.put("lat", dispatchDetail.getDestinationLatitude());
                    stopover.put("lon", dispatchDetail.getDestinationLongitude());
                    stopoverList.add(stopover);

                    if(dispatchDetail.getDispatchDetailStatus() == DispatchDetailStatus.DELIVERY_DELAY){
                        String comment = null;
                        if(dispatchDetail.getDestinationId() !=null){
                            comment = deliveryDestinationRepository.findByIdWithThrow(dispatchDetail.getDestinationId()).getComment();
                        }
                        Issue issue = Issue.of(
                            dispatchCodeId,
                            dispatch.getId(),
                            dispatch.getSmName(),
                            dispatchDetail.getTransportOrder().getCustomerAddress(),
                            dispatchDetail.getDestinationId() != null ? dispatchDetail.getDestinationId() : null,
                            comment);
                        issueList.add(issue);
                    }
                }
                List<Map<String, Double>> coordinates = new ArrayList<>();
                for (Coordinate coordinateByPath : dispatch.getPath().getCoordinates()) {
                    Map<String, Double> coordinate = new HashMap<>();
                    coordinate.put("lat", coordinateByPath.getY());
                    coordinate.put("lon", coordinateByPath.getX());
                    coordinates.add(coordinate);
                }

                return DispatchResponse.of(dispatch.getId(), dispatch.getDeliveryStatus().getDescription(), dispatch.getSmName(), dispatch.getCompletedOrderCount(), dispatch.getDeliveryOrderCount(), (int) progressionRate,stopoverList,coordinates );
            }).toList();

        return DispatchListResponse.of(dispatchNumber.getDispatchNumber(), dispatchNumber.getDispatchName(),(int) totalProgressionRate,totalCompletedOrderCount,totalDeliveryOrderCount,issueList.size(),startStopover,dispatchResponseList,issueList);
    }
}
