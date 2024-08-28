package com.team2.finalproject.domain.dispatchdetail.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.team2.finalproject.domain.dispatchdetail.model.type.DestinationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchDetailResponse {

    @Schema(example = "1", description = "기사 명")
    private Long smName;

    @Schema(example = "010-1234-5678", description = "기사 전화번호")
    private String smPhoneNumber;

    @Schema(example = "80", description = "용적률")
    private Double floorAreaRatio;

    @Schema(example = "WING_BODY", description = "차종")
    private String vehicleType;

    @Schema(example = "5", description = "차량 톤")
    private Double vehicleTon;

    @Schema(example = "75", description = "진행률")
    private Double progressionRate;

    @Schema(example = "10", description = "완료주문")
    private Integer completedOrderCount;

    @Schema(example = "20", description = "주문 수")
    private Integer deliveryOrderCount;

    @Schema(example = "120", description = "주행시간")
    private Integer totalTime;

    @Schema(example = "No issues", description = "이슈 및 메모")
    private String issue;

    private StartStopover startStopover;

    private List<DispatchDetail> dispatchDetailList;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartStopover {
        @Schema(example = "1", description = "센터 ID")
        private Long centerId;

        @Schema(example = "Main Center", description = "센터 이름")
        private String centerName;

        @Schema(example = "37.5995", description = "위도")
        private Double lat;

        @Schema(example = "127.1116", description = "경도")
        private Double lon;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DispatchDetail {
        @Schema(example = "1", description = "배차 상세 ID")
        private Long dispatchDetailId;

        @Schema(example = "TRANSPORTATION_START", description = "현재 배송 상태")
        private String dispatchDetailStatus;

        @Schema(example = "2024-08-27T09:00:00", description = "작업 시작 시간")
        private LocalDateTime operationStartTime;

        @Schema(example = "2024-08-27T12:00:00", description = "작업 종료 시간")
        private LocalDateTime operationEndTime;

        @Schema(example = "2024-08-27T08:30:00", description = "예상 작업 시작 시간")
        private LocalDateTime expectationOperationStartTime;

        @Schema(example = "2024-08-27T11:30:00", description = "예상 작업 종료 시간")
        private LocalDateTime expectationOperationEndTime;

        @Schema(example = "30", description = "예상 이동 시간")
        private Integer ett;

        @Schema(example = "CENTER", description = "배송처 타입")
        private DestinationType destinationType;

        @Schema(example = "null", description = "배송처 ID")
        private Long destinationId;

        @Schema(example = "Handle with care", description = "배송처 비고")
        private String destinationComment;

        @Schema(example = "123 Main St", description = "지번 주소")
        private String address;

        @Schema(example = "1001", description = "운송 실행 주문 ID")
        private Long transportOrderId;

        @Schema(example = "37.5995", description = "위도")
        private Double lat;

        @Schema(example = "127.1116", description = "경도")
        private Double lon;
    }
}