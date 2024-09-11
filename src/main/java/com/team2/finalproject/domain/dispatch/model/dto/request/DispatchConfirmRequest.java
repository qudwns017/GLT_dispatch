package com.team2.finalproject.domain.dispatch.model.dto.request;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchConfirmRequest.DispatchList.DispatchDetailList;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchdetail.model.type.DestinationType;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;

import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.global.util.response.Coordinate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.locationtech.jts.geom.LineString;

public record DispatchConfirmRequest(
        @Schema(example = "240808C001#1", description = "배차 코드 (배차 번호)", requiredMode = Schema.RequiredMode.REQUIRED)
        String dispatchCode,

        @Schema(example = "인플루언서 A 긴급건", description = "배차명", requiredMode = Schema.RequiredMode.REQUIRED)
        String dispatchName,

        @Schema(example = "2023-08-30T08:00:00", description = "상차 시작 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime loadingStartTime,

        @Schema(description = "경로별 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
        List<DispatchList> dispatchList
) {
    public record DispatchList(
            @Schema(example = "1", description = "기사 ID")
            long smId,

            @Schema(example = "14:00:00", description = "휴식 시작 시간")
            LocalTime breakStartTime,

            @Schema(example = "15:00:00", description = "휴식 종료 시간")
            LocalTime breakEndTime,

            @Schema(example = "4", description = "휴식 경유지 위치 (해당 경유지의 바로 앞)")
            int restingStopover,

            @Schema(description = "배송 상세 리스트")
            List<DispatchDetailList> dispatchDetailList,

            @Schema(description = "상세 경로 좌표",
                    example = """
                            [
                                {
                                    "lon": 127.1116,
                                    "lat": 37.5995
                                },
                                {
                                    "lon": 127.1120,
                                    "lat": 37.6000
                                },
                                {
                                    "lon": 127.1135,
                                    "lat": 37.6012
                                }
                            ]
                            """)
            List<Coordinate> coordinates
    ) {
        public record DispatchDetailList(
                @Schema(example = "홍길동", description = "기사 이름")
                String smName,

                @Schema(example = "30", description = "예상 이동 시간 (분)")
                int ett,

                @Schema(example = "2023-08-30T09:00:00", description = "예상 작업 시작 시간")
                LocalDateTime expectationOperationStartTime,

                @Schema(example = "2023-08-30T09:30:00", description = "예상 작업 종료 시간")
                LocalDateTime expectationOperationEndTime,

                @Schema(example = "1", description = "배송처 ID")
                Long deliveryDestinationId,

                @Schema(example = "010-1234-5678", description = "고객 전화번호")
                String phoneNumber,

                @Schema(example = "ORD123456", description = "주문 번호")
                String orderNumber,

                @Schema(example = "2024-08-08", description = "주문접수일")
                LocalDate orderDate,

                @Schema(example = "37.5995", description = "경유지 위도")
                double lat,

                @Schema(example = "127.1116", description = "경유지 경도")
                double lon,

                @Schema(example = "3.3", description = "이동 거리 (km)")
                double distance,

                @Schema(example = "택배", description = "배송 유형 (지입, 용차, 택배)")
                String deliveryType,

                @Schema(example = "C0029384889", description = "운송장 번호")
                String shipmentNumber,

                @Schema(example = "배송", description = "주문 유형 (배송, 수거)")
                String orderType,

                @Schema(example = "2023-08-28", description = "작업 희망일")
                LocalDate serviceRequestDate,

                @Schema(example = "14:00:00", description = "희망 도착 시간 (HH:MM:SS)")
                LocalTime serviceRequestTime,

                @Schema(example = "홍길동", description = "고객 이름")
                String clientName,

                @Schema(example = "서울특별시 강남구 강남동 37", description = "주소")
                String lotNumberAddress,

                @Schema(example = "서울특별시 강남구 강남대로 123", description = "도로명 주소")
                String roadAddress,

                @Schema(example = "강남빌딩 3층", description = "상세 주소")
                String detailAddress,

                @Schema(example = "06000", description = "우편번호")
                String zipcode,

                @Schema(example = "1.5", description = "볼륨 (m³)")
                double volume,

                @Schema(example = "10.0", description = "중량 (kg)")
                double weight,

                @Schema(example = "조심히 다뤄주세요.", description = "고객 전달 사항")
                String note,

                @Schema(example = "20", description = "예상 작업 시간 (분)")
                int expectedServiceDuration,

                @Schema(example = "상품명 A", description = "상품명")
                String productName,

                @Schema(example = "ST05", description = "상품 코드")
                String productCode,

                @Schema(example = "3", description = "상품 수량")
                int productQuantity
        ) {
        }
    }

    public static DispatchNumber toDispatchNumberEntity(
            DispatchConfirmRequest request,
            Users users,
            Center center
    ) {
        return DispatchNumber.builder()
                .dispatchNumber(request.dispatchCode())
                .dispatchName(request.dispatchName())
                .loadingStartTime(request.loadingStartTime())
                .status(DispatchNumberStatus.WAITING)
                .manager(users)
                .center(center)
                .build();
    }

    public static Dispatch toDispatchEntity(
            DispatchConfirmRequest request,
            DispatchNumber dispatchNumber,
            Sm sm,
            Center center,
            double totalVolume,
            double totalWeight,
            double totalDistance,
            int totalTime,
            LineString path,
            DispatchList list
    ) {
        return Dispatch.builder()
                .dispatchNumber(dispatchNumber)
                .sm(sm)
                .smName(sm.getSmName())
                .completedOrderCount(0)
                .deliveryOrderCount(request.dispatchList.size())
                .destinationCount(request.dispatchList.size())
                .loadingRate(
                        totalVolume / sm.getVehicle().getMaxLoadVolume()) // 전체 용적률 평균, 용적률(볼륨) = 볼륨 합 / 차량 부피 * 100
                .totalVolume(totalVolume) // 볼륨 합
                .totalWeight(totalWeight) // 무게 합
                .totalDistance(totalDistance) // 거리 합
                .departurePlaceCode(center.getCenterCode()) // 센터코드
                .departurePlaceName(center.getCenterName()) // 센터명
                .departureTime(request.loadingStartTime.plusMinutes(center.getDelayTime())) // 상차 시작 + center의 delayTime
                .breakStartTime(list.breakStartTime())
                .breakEndTime(list.breakEndTime())
                .restingStopover(list.restingStopover())
                .arrivalTime(null)
                .deliveryStatus(DispatchStatus.WAITING)
                .issue("")
                .totalTime(LocalTime.of(totalTime / 60, totalTime % 60)) // 시간 합
                .path(path)
                .build();
    }

    public static DispatchDetail toDispatchDetailEntity(
            DispatchDetailList list, Dispatch dispatch,
            TransportOrder transportOrder
    ) {
        return DispatchDetail.builder()
                .dispatch(dispatch)
                .transportOrder(transportOrder)
                .destinationId(list.deliveryDestinationId())
                .destinationLatitude(list.lat())
                .destinationLongitude(list.lon())
                .distance(list.distance())
                .expectationOperationStartTime(list.expectationOperationStartTime())
                .expectationOperationEndTime(list.expectationOperationEndTime())
                .loadingCompletionTime(null)
                .operationStartTime(list.expectationOperationStartTime()) // 작업 시작 시간 : 경로 계산 결과 필요
                .operationEndTime(list.expectationOperationEndTime())
                .transportationStartTime(null)
                .destinationType(list.deliveryDestinationId() != null ? DestinationType.DELIVERY_DESTINATION
                        : DestinationType.CUSTOMER_DESTINATION)
                .dispatchDetailStatus(DispatchDetailStatus.WORK_WAITING)
                .build();
    }

    public static TransportOrder toTransportOrderEntity(
            DispatchDetailList list,
            Center center
    ) {
        return TransportOrder.builder()
                .center(center)
                .shipmentNumber(list.shipmentNumber())
                .orderNumber(list.orderNumber())
                .orderType(list.orderType())
                .orderDate(list.orderDate())
                .deliveryType(list.deliveryType())
                .productCode(list.productCode())
                .productName(list.productName())
                .productCount(list.productQuantity())
                .volume(list.volume())
                .weight(list.weight())
                .customerName(list.clientName())
                .customerNotes(list.note())
                .customerPhoneNumber(list.phoneNumber())
                .lotNumberAddress(list.lotNumberAddress())
                .zipCode(list.zipcode())
                .roadAddress(list.roadAddress())
                .detailAddress(list.detailAddress())
                .requestedArrivalTime(list.serviceRequestTime())
                .requestedWorkDate(list.serviceRequestDate())
                .estimatedWorkTime(
                        LocalTime.of(list.expectedServiceDuration() / 60, list.expectedServiceDuration() % 60))
                .isPending(false)
                .smName(list.smName)
                .build();
    }
}
