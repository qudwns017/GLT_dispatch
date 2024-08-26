package com.team2.finalproject.domain.dispatchdetail.model.entity;

import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatchdetail.model.type.DestinationType;
import com.team2.finalproject.domain.dispatchdetail.model.type.DispatchDetailStatus;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispatchDetail extends BaseEntity {

    @Column(nullable = true)
    private Long destinationId; // 도착지 id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DestinationType destinationType;  // 도착지 종류

    @Column(nullable = false)
    private double destinationLatitude; // 도착지위도

    @Column(nullable = false)
    private double destinationLongitude; // 도착지경도

    @Column(nullable = false)
    private LocalDateTime expectationOperationStartTime; // 예정작업시작시간

    @Column(nullable = false)
    private LocalDateTime expectationOperationEndTime; // 예정작업종료시간

    @Column(nullable = false)
    private LocalDateTime operationStartTime; // 작업시작시간

    @Column(nullable = false)
    private LocalDateTime operationEndTime; // 작업종료시간

    @Column(nullable = false)
    private Long distance; //이동거리

    @Getter
    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchDetailStatus dispatchDetailStatus = DispatchDetailStatus.PENDING; // 배차상세상태

    @Column(nullable = true)
    private LocalDateTime transportationStartTime; // 운송시작시간

    @Column(nullable = true)
    private LocalDateTime loadingCompletionTime; // 상차완료시간

    @ManyToOne(fetch = FetchType.LAZY)
    private Dispatch dispatch;

    @OneToOne(cascade = CascadeType.REMOVE)
    private TransportOrder transportOrder;
}
