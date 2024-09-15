package com.team2.finalproject.domain.dispatchnumber.model.dto.response;

import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class DispatchListResponse {

    @Schema(example = "240808C001#1", description = "배차코드")
    private String dispatchCode;
    @Schema(example = "서울 배차", description = "배차명")
    private String dispatchName;
    @Schema(example = "80", description = "총 진행률")
    private int totalProgressionRate;
    @Schema(example = "5", description = "총 완료주문수")
    private int totalCompletedOrderNum;
    @Schema(example = "13", description = "총 주문수")
    private int totalOrderNum;
    @Schema(example = "3", description = "이슈 주문수")
    private int issueOrderNum;
    private StartStopover startStopover;
    private List<DispatchSimpleResponse> dispatchList;
    private List<Issue> issueList;

    private DispatchListResponse(String dispatchCode, String dispatchName, int totalProgressionRate,
                                 int totalCompletedOrderNum, int totalOrderNum, int issueOrderNum,
                                 StartStopover startStopover, List<DispatchSimpleResponse> dispatchList,
                                 List<Issue> issueList) {
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

    public static DispatchListResponse of(String dispatchCode, String dispatchName, int totalProgressionRate,
                                          int totalCompletedOrderNum, int totalOrderNum, int issueOrderNum,
                                          StartStopover startStopover, List<DispatchSimpleResponse> dispatchList,
                                          List<Issue> issueList) {
        return new DispatchListResponse(dispatchCode, dispatchName, totalProgressionRate, totalCompletedOrderNum,
                totalOrderNum, issueOrderNum, startStopover, dispatchList, issueList);
    }

    @Getter
    @NoArgsConstructor
    public static class StartStopover {
        @Schema(example = "1", description = "센터id")
        private Long centerId;
        @Schema(example = "서울시 강동구 천호동", description = "주소")
        private String address;
        @Schema(example = "37.5409", description = "위도")
        private Double lat;
        @Schema(example = "127.1263", description = "경도")
        private Double lon;
        @Schema(example = "60", description = "지연시간(분)")
        private int delayTime;

        private StartStopover(Long centerId, String address, Double lat, Double lon, int delayTime) {
            this.centerId = centerId;
            this.address = address;
            this.lat = lat;
            this.lon = lon;
            this.delayTime = delayTime;
        }

        public static StartStopover of(Long centerId, String address, Double lat, Double lon, int delayTime) {
            return new StartStopover(centerId, address, lat, lon, delayTime);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class DispatchSimpleResponse {
        @Schema(example = "2", description = "배차id")
        private Long dispatchId;
        @Schema(example = "WORK_COMPLETED", description = "배차상태")
        private DispatchStatus dispatchStatus;
        @Schema(example = "홍길동", description = "기사명")
        private String smName;
        @Schema(example = "3", description = "완료 주문수")
        private int completedOrderNum;
        @Schema(example = "10", description = "주문수")
        private int orderNum;
        @Schema(example = "30", description = "진행률")
        private int progressionRate;
        @ArraySchema(
                arraySchema = @Schema(description = "경유지 리스트"),
                schema = @Schema(
                        example = "{\"lat\": 37.5665, \"lon\": 126.9780}",
                        description = "경유지의 위도와 경도"
                )
        )
        private List<Map<String, Double>> stopoverList;
        @ArraySchema(
                arraySchema = @Schema(description = "경로 좌표 리스트"),
                schema = @Schema(
                        example = "{\"lat\": 37.5665, \"lon\": 126.9780}",
                        description = "경로 좌표"
                )
        )
        private List<Map<String, Double>> coordinates;

        private DispatchSimpleResponse(Long dispatchId, DispatchStatus dispatchStatus, String smName,
                                       int completedOrderNum,
                                       int orderNum, int progressionRate, List<Map<String, Double>> stopoverList,
                                       List<Map<String, Double>> coordinates) {
            this.dispatchId = dispatchId;
            this.dispatchStatus = dispatchStatus;
            this.smName = smName;
            this.completedOrderNum = completedOrderNum;
            this.orderNum = orderNum;
            this.progressionRate = progressionRate;
            this.stopoverList = stopoverList;
            this.coordinates = coordinates;
        }

        public static DispatchSimpleResponse of(Long dispatchId, DispatchStatus dispatchStatus, String smName,
                                                int completedOrderNum, int orderNum, int progressionRate,
                                                List<Map<String, Double>> stopoverList,
                                                List<Map<String, Double>> coordinates) {
            return new DispatchSimpleResponse(dispatchId, dispatchStatus, smName, completedOrderNum, orderNum,
                    progressionRate, stopoverList, coordinates);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Issue {
        @Schema(example = "2", description = "배차코드id")
        private Long dispatchCodeId;
        @Schema(example = "2", description = "배차id")
        private Long dispatchId;
        @Schema(example = "홍길동", description = "기사명")
        private String smName;
        @Schema(example = "서울시 강동구 천호동", description = "주소")
        private String address;
        @Schema(example = "2", description = "배송처 id(null이면 배송처정보 없음)")
        private Long deliveryDestinationId;
        @Schema(example = "20", description = "지연된 시간")
        private Integer delayedTime;

        private Issue(Long dispatchCodeId, Long dispatchId, String smName, String address, Long deliveryDestinationId,
                      Integer delayedTime) {
            this.dispatchCodeId = dispatchCodeId;
            this.dispatchId = dispatchId;
            this.smName = smName;
            this.address = address;
            this.deliveryDestinationId = deliveryDestinationId;
            this.delayedTime = delayedTime;
        }

        public static Issue of(Long dispatchCodeId, Long dispatchId, String smName, String address,
                               Long deliveryDestinationId, Integer delayedTime) {
            return new Issue(dispatchCodeId, dispatchId, smName, address, deliveryDestinationId, delayedTime);
        }

    }
}
