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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record DispatchConfirmRequest(
        String dispatchCode,
        String dispatchName,
        LocalDateTime loadingStartTime,
        List<DispatchList> dispatchList
) {
    public record DispatchList(
            long smId,
            long orderNum,
            int mileage,
            int totalTime,
            LocalDateTime departureTime,
            List<DispatchDetailList> dispatchDetailList
    ) {
        public record DispatchDetailList(
                int loadingRate,
                String smName,
                boolean errorYn,  // 진입 조건
                int ett,  // 예상 이동 시간 (분)
                LocalDateTime expectationOperationStartTime,  // 예상 작업 시작 시간
                LocalDateTime expectationOperationEndTime,  // 예상 작업 종료 시간
                Long deliveryDestinationId,  // 배송처 ID (배송처 코드)
                String managerName,  // 담당자 이름
                String phoneNumber,  // 담당자 전화번호
                String orderNumber,
                double lat,  // 경유지 위도
                double lon,  // 경유지 경도
                double distance,  // 이동 거리 (km)
                String deliveryType,  // 배송유형 ("지입", "용차", "택배")
                String shipmentNumber,  // 운송장 번호
                String clientOrderKey,  // 업체 주문 번호
                String orderType,  // 주문 유형 ("배송", "수거")
                LocalDate receivedDate,  // 주문 접수일 (YYYYMMDD)
                LocalDate serviceRequestDate,  // 작업 희망일 (YYYYMMDD)
                LocalTime serviceRequestTime,  // 희망 도착 시간 (HH:MM:SS)
                String clientName,  // 고객 이름
                String contact,  // 고객 연락처
                String address,  // 주소
                String detailAddress,  // 상세 주소
                String roadAddress,
                String zipcode,  // 우편번호
                double volume,  // 볼륨
                double weight,  // 중량
                String note,  // 고객 전달 사항
                int expectedServiceDuration,  // 예상 작업 시간 (분)
                String productName,  // 상품명
                String productCode,  // 상품 코드
                int productQuantity  // 상품 수량
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
                .users(users)
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
            int totalTime
    ) {
        return Dispatch.builder()
                .dispatchNumber(dispatchNumber)
                .sm(sm)
                .smName(sm.getSmName())
                .completedOrderCount(0)
                .deliveryOrderCount(request.dispatchList.size())
                .destinationCount(request.dispatchList.size())
                .loadingRate(totalVolume / sm.getVehicle().getMaxLoadVolume()) // 전체 용적률 평균, 용적률(볼륨) = 볼륨 합 / 차량 부피 * 100
                .totalVolume(totalVolume) // 볼륨 합
                .totalWeight(totalWeight) // 무게 합
                .totalDistance(totalDistance) // 거리 합
                .departurePlaceCode(center.getCenterCode()) // 센터코드
                .departurePlaceName(center.getCenterName()) // 센터명
                .departureTime(request.loadingStartTime.plusMinutes(center.getDelayTime())) // 상차 시작 + center의 delayTime
                .arrivalTime(null)
                .deliveryStatus(DispatchStatus.WAITING)
                .issue("")
                .totalTime(LocalTime.of(0, totalTime)) // 시간 합
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
                .deliveryType(list.deliveryType())
                .productCode(list.productCode())
                .productName(list.productName())
                .productCount(list.productQuantity())
                .volume(list.volume())
                .weight(list.weight())
                .customerName(list.clientName())
                .customerNotes(list.note())
                .customerPhoneNumber(list.phoneNumber())
                .customerAddress(list.address())
                .zipCode(list.zipcode())
                .roadAddress(list.roadAddress())
                .detailAddress(list.detailAddress())
                .requestedArrivalTime(list.serviceRequestTime())
                .requestedWorkDate(list.serviceRequestDate())
                .estimatedWorkTime(LocalTime.of(0, list.expectedServiceDuration()))
                .isPending(false)
                .smName(list.smName)
                .build();
    }
}
