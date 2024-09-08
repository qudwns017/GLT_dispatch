package com.team2.finalproject.domain.dispatchdetail.model.dto.response;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatchdetail.model.type.DestinationType;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.domain.vehicle.model.type.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchDetailResponse {

    @Schema(example = "1", description = "기사 명")
    private String smName;

    @Schema(example = "010-1234-5678", description = "기사 전화번호")
    private String smPhoneNumber;

    @Schema(example = "80", description = "용적률")
    private double floorAreaRatio;

    @Schema(example = "WING_BODY", description = "차종")
    private VehicleType vehicleType;

    @Schema(example = "5", description = "차량 톤")
    private Double vehicleTon;

    @Schema(example = "75", description = "진행률")
    private int progressionRate;

    @Schema(example = "10", description = "완료주문")
    private int completedOrderCount;

    @Schema(example = "20", description = "주문 수")
    private int deliveryOrderCount;

    @Schema(example = "2:00:00", description = "주행시간")
    private LocalTime totalTime;

    @Schema(example = "No issues", description = "이슈 및 메모")
    private String issue;

    @Schema(example = "13:00", description = "휴식 시작 시간")
    private LocalTime breakStartTime;

    @Schema(example = "15:00", description = "휴식 종료 시간")
    private LocalTime breakEndTime;

    @Schema(example = "3", description = "휴식 경유지 위치 (해당 경유지의 바로 앞)")
    private int restingStopover;

    private StartStopover startStopover;

    private List<DispatchDetail> dispatchDetailList;

    @Getter
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

        @Schema(example = "12:00:00", description = "운송 시작 시간")
        private LocalDateTime departureTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DispatchDetail {
        @Schema(example = "1", description = "배차 상세 ID")
        private Long dispatchDetailId;

        @Schema(example = "TRANSPORTATION_START", description = "현재 배송 상태")
        private DispatchDetailStatus dispatchDetailStatus;

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

    public static DispatchDetailResponse.StartStopover getStartStopover (Center center, LocalDateTime departureTime) {
        return StartStopover.builder()
                .centerId(center.getId())
                .centerName(center.getCenterName())
                .lat(center.getLatitude())
                .lon(center.getLongitude())
                .departureTime(departureTime)
                .build();
    }

    public static DispatchDetailResponse.DispatchDetail getDispatchDetail(
            com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail dispatchDetail,
             TransportOrder transportOrder, String comment) {

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
                .destinationComment(comment)
                .address(transportOrder.getLotNumberAddress())
                .transportOrderId(transportOrder.getId())
                .lat(dispatchDetail.getDestinationLatitude())
                .lon(dispatchDetail.getDestinationLongitude())
                .build();
    }

    public static DispatchDetailResponse of(
            Dispatch dispatch, Sm sm, Users users, Vehicle vehicle,
            DispatchDetailResponse.StartStopover startStopover,
            List<DispatchDetailResponse.DispatchDetail> dispatchDetailList) {

        return DispatchDetailResponse.builder()
                .smName(sm.getSmName())
                .smPhoneNumber(users.getPhoneNumber())
                .floorAreaRatio(dispatch.getLoadingRate())
                .vehicleType(vehicle.getVehicleType())
                .vehicleTon(vehicle.getVehicleTon())
                .progressionRate(calcProgress(dispatch.getDeliveryOrderCount(), dispatch.getCompletedOrderCount()))
                .completedOrderCount(dispatch.getCompletedOrderCount())
                .deliveryOrderCount(dispatch.getDeliveryOrderCount())
                .totalTime(dispatch.getTotalTime())
                .issue(dispatch.getIssue())
                .breakStartTime(dispatch.getBreakStartTime())
                .breakEndTime(dispatch.getBreakEndTime())
                .restingStopover(dispatch.getRestingStopover())
                .startStopover(startStopover)
                .dispatchDetailList(dispatchDetailList)
                .build();
    }

    private static DispatchDetailStatus getDispatchDetailStatus
            (com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail dispatchDetail) {
        if(dispatchDetail.isResting()) {
            return DispatchDetailStatus.RESTING;
        }else {
            return dispatchDetail.getDispatchDetailStatus();
        }
    }

    // 진행률 계산
    private static int calcProgress(int totalOrder, int completedOrder) {
        if (totalOrder == 0) {
            return 0;
        }
        return (int) Math.round((double) completedOrder / totalOrder * 100);
    }
}