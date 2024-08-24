package com.team2.finalproject.domain.dispatchnumber.model.dto.response;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DispatchListResponse {

    private String dispatchCode;
    private String dispatchName;
    private int totalProgressionRate;
    private int totalCompletedOrderNum;
    private int totalOrderNum;
    private int issueOrderNum;
    private StartStopover startStopover;
    private List<DispatchResponse> dispatchList;
    private List<Issue> issueList;

    private DispatchListResponse(String dispatchCode,String dispatchName, int totalProgressionRate,int totalCompletedOrderNum, int totalOrderNum, int issueOrderNum, StartStopover startStopover,List<DispatchResponse> dispatchList,List<Issue> issueList){
        this.dispatchCode = dispatchCode;
        this.dispatchName = dispatchName;
        this.totalProgressionRate = totalProgressionRate;
        this.totalCompletedOrderNum = totalCompletedOrderNum;
        this.totalOrderNum = totalOrderNum;
        this.issueOrderNum = issueOrderNum;
        this.startStopover = startStopover;
        this.dispatchList = dispatchList;
        this.issueList = issueList;
    }

    public static DispatchListResponse of(String dispatchCode,String dispatchName, int totalProgressionRate,int totalCompletedOrderNum, int totalOrderNum, int issueOrderNum, StartStopover startStopover,List<DispatchResponse> dispatchList,List<Issue> issueList){
        return new DispatchListResponse(dispatchCode,dispatchName,totalProgressionRate,totalCompletedOrderNum,totalOrderNum,issueOrderNum,startStopover,dispatchList,issueList);
    }

    @Getter
    @NoArgsConstructor
    public static class StartStopover{
        private Long centerId;
        private String address;
        private Double lat;
        private Double lon;
        private LocalTime delayTime;

        private StartStopover(Long centerId,String address,Double lat,Double lon,LocalTime delayTime){
            this.centerId = centerId;
            this.address = address;
            this.lat = lat;
            this.lon = lon;
            this.delayTime = delayTime;
        }

        public static StartStopover of(Long centerId,String address,Double lat,Double lon,LocalTime delayTime){
            return new StartStopover(centerId,address,lat,lon,delayTime);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class DispatchResponse{
        private Long dispatchId;
        private String dispatchStatus;
        private String smName;
        private int completedOrderNum;
        private int orderNum;
        private int progressionRate;
        private List<Map<String, Double>> stopoverList;
        private List<Map<String, Double>> coordinates;

        private DispatchResponse(Long dispatchId,String dispatchStatus,String smName, int completedOrderNum,int orderNum,int progressionRate,List<Map<String, Double>> stopoverList,List<Map<String, Double>> coordinates){
            this.dispatchId = dispatchId;
            this.dispatchStatus = dispatchStatus;
            this.smName = smName;
            this.completedOrderNum = completedOrderNum;
            this.orderNum = orderNum;
            this.progressionRate = progressionRate;
            this.stopoverList = stopoverList;
            this.coordinates = coordinates;
        }

        public static DispatchResponse of(Long dispatchId,String dispatchStatus,String smName, int completedOrderNum,int orderNum,int progressionRate,List<Map<String, Double>> stopoverList,List<Map<String, Double>> coordinates){
            return new DispatchResponse(dispatchId,dispatchStatus,smName,completedOrderNum,orderNum,progressionRate,stopoverList,coordinates);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Issue{
        private Long dispatchCodeId;
        private Long dispatchId;
        private String smName;
        private String address;
        private Long deliveryDestinationId;
        private String issue;

        private Issue(Long dispatchCodeIdh,Long dispatchId,String smName,String address,Long deliveryDestinationId,String issue){
            this.dispatchCodeId = dispatchCodeIdh;
            this.dispatchId = dispatchId;
            this.smName = smName;
            this.address = address;
            this.deliveryDestinationId = deliveryDestinationId;
            this.issue = issue;
        }

        public static Issue of(Long dispatchCodeIdh,Long dispatchId,String smName,String address,Long deliveryDestinationId,String issue){
            return new Issue(dispatchCodeIdh,dispatchId,smName,address,deliveryDestinationId,issue);
        }

    }
}
