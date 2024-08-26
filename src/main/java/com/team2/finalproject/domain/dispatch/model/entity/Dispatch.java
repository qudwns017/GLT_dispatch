package com.team2.finalproject.domain.dispatch.model.entity;

import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.*;
import org.locationtech.jts.geom.LineString;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Dispatch extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String smName; // 지입기사명

    @Column(nullable = false)
    private int destinationCount; // 도착지수

    @Setter
    @Column(nullable = false)
    private int deliveryOrderCount; // 운송오더수

    @Builder.Default
    @Column(nullable = false)
    private int completedOrderCount = 0; // 완료오더수

    @Column(nullable = false)
    private Long totalDistance;   //총거리

    @Column(nullable = false)
    private LocalTime totalTime; // 총 소요시간

    @Column(nullable = false, length = 4)
    private String departurePlaceCode; // 출발지코드

    @Column(nullable = false, length = 50)
    private String departurePlaceName; // 출발지명

    @Column(nullable = true)
    private LocalDateTime arrivalTime; // 도착시간   주행 완료 후의 시간

    @Column(nullable = false)
    private int totalWeight; // 총 중량

    @Column(nullable = false)
    private int totalVolume; // 총 볼륨

    @Column(nullable = false)
    private int loadingRate; // 적재율

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus deliveryStatus = DispatchStatus.IN_TRANSIT; // 배차상태

    @Column(columnDefinition = "geometry(LineString)",nullable = false)
    private LineString path;

    @Column(nullable = false, length = 100)
    private String issue; // 이슈

    @ManyToOne(fetch = FetchType.LAZY)
    private DispatchNumber dispatchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sm sm;

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.REMOVE)
    private List<DispatchDetail> dispatchDetailList;
}
