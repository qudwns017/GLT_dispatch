package com.team2.finalproject.domain.dispatch.model.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DispatchUpdateResponse {

    @Schema(example = "30.5", description = "주행거리 (km)")
    private Double mileage;  // 주행거리 (km)
    @Schema(example = "34", description = "주행시간 (분)")
    private Long totalTime; // 주행시간 (분)
    private StartStopover startStopover;
    private List<DispatchDetailResponse> dispatchDetailList;
    @ArraySchema(
        arraySchema = @Schema(description = "경로 좌표 리스트"),
        schema = @Schema(
            example = "{\"lat\": 37.5665, \"lon\": 126.9780}",
            description = "경로 좌표"
        )
    )
    private List<Map<String,Double>> coordinates;

    private DispatchUpdateResponse(Double mileage,Long totalTime, StartStopover startStopover, List<DispatchDetailResponse> dispatchDetailList,List<Map<String,Double>> coordinates){
        this.mileage = mileage;
        this.totalTime = totalTime;
        this.startStopover = startStopover;
        this.dispatchDetailList = dispatchDetailList;
        this.coordinates = coordinates;
    }

    public static DispatchUpdateResponse of(Double mileage,Long totalTime, StartStopover startStopover, List<DispatchDetailResponse> dispatchDetailList,List<Map<String,Double>> coordinates){
        return new DispatchUpdateResponse(mileage,totalTime, startStopover, dispatchDetailList, coordinates);
    }

    @Getter
    @NoArgsConstructor
    public static class StartStopover{
        @Schema(example = "서울시 강동구 천호동", description = "주소")
        private String address;
        @Schema(example = "37.5409", description = "위도")
        private Double lat;
        @Schema(example = "127.1263", description = "경도")
        private Double lon;
        @Schema(example = "60", description = "지연시간(분)")
        private int delayTime;
        @Schema(example = "2023-06-15T09:00:00", description = "예상작업시작시간")
        private LocalDateTime expectationOperationStartTime;
        @Schema(example = "2023-06-15T10:00:00", description = "예상작업종료시간")
        private LocalDateTime expectationOperationEndTime;

        private StartStopover(String address,Double lat,Double lon,int delayTime,LocalDateTime expectationOperationStartTime,LocalDateTime expectationOperationEndTime){
            this.address = address;
            this.lat = lat;
            this.lon = lon;
            this.delayTime = delayTime;
            this.expectationOperationStartTime = expectationOperationStartTime;
            this.expectationOperationEndTime = expectationOperationEndTime;

        }

        public static DispatchUpdateResponse.StartStopover of(String address,Double lat,Double lon,int delayTime,LocalDateTime expectationOperationStartTime,LocalDateTime expectationOperationEndTime){
            return new DispatchUpdateResponse.StartStopover(address,lat,lon,delayTime,expectationOperationStartTime,expectationOperationEndTime);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class DispatchDetailResponse{

        @Schema(example = "서울시 강동구 천호동", description = "주소")
        private String address;
        @Schema(example = "27", description = "예상 이동 시간 (분)")
        private Long ett;
        @Schema(example = "2023-06-15T10:27:00", description = "예상작업시작시간")
        private LocalDateTime expectationOperationStartTime;
        @Schema(example = "2023-06-15T10:57:00", description = "예상작업종료시간")
        private LocalDateTime expectationOperationEndTime;
        @Schema(example = "30", description = "예상작업시간")
        private int expectedServiceDuration;
        @Schema(example = "37.5409", description = "위도")
        private Double lat;
        @Schema(example = "127.1263", description = "경도")
        private Double lon;
        @Schema(example = "30.4", description = "이동거리 (km)")
        private Double distance;

        private DispatchDetailResponse(String address,Long ett,LocalDateTime expectationOperationStartTime,LocalDateTime expectationOperationEndTime,int expectedServiceDuration,Double lat,Double lon,Double distance){
            this.address = address;
            this.ett = ett;
            this.expectationOperationStartTime = expectationOperationStartTime;
            this.expectationOperationEndTime = expectationOperationEndTime;
            this.expectedServiceDuration = expectedServiceDuration;
            this.lat = lat;
            this.lon = lon;
            this.distance = distance;
        }

        public static DispatchDetailResponse of(String address,Long ett,LocalDateTime expectationOperationStartTime,LocalDateTime expectationOperationEndTime,int expectedServiceDuration,Double lat,Double lon,Double distanceTypeMeter){
            return new DispatchDetailResponse(address,ett,expectationOperationStartTime,expectationOperationEndTime,expectedServiceDuration,lat,lon, distanceTypeMeter / 1000);
        }


    }
}
