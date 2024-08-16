package com.team2.finalproject.domain.dispatchdetail.model.entity;

import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispatchDetail extends BaseEntity {

    @Column(nullable = false)
    private Long dispatchScheduleId; // 배차계획id

    @Column(nullable = false)
    private Long transportOrderId; // 운송실행주문 id

    @Column(nullable = false)
    private double locationLatitude; // 도착지위도

    @Column(nullable = false)
    private double locationLongitude; // 도착지경도

    @Column(nullable = false, length = 50)
    private String locationName; // 도착지명

    @Column(nullable = false)
    private int deliveryOrder; // 배송순번

    @Column(nullable = false)
    private LocalDateTime actualDepartureTime; // 출발예정시간 (Field5)

    @Column(nullable = false)
    private LocalDateTime estimatedArrivalTime; // 도착예정시간 (Field45)

    @Column(nullable = false)
    private Long distance; //이동거리

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchDetailStatus dispatchDetailStatus = DispatchDetailStatus.PENDING; // 배차상세상태

    @Column(nullable = true)
    private LocalDateTime transportationStartTime; // 운송시작시간 (Field3)

    @Column(nullable = true)
    private LocalDateTime loadingCompletionTime; // 상차완료시간

    @Column(nullable = true)
    private LocalDateTime unloadingCompletionTime; // 하차완료시간
}
