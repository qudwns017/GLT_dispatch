package com.team2.finalproject.domain.dispatch.model.entity;

import com.team2.finalproject.domain.dispatch.model.dto.request.IssueRequest;
import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.domain.dispatchdetail.model.entity.DispatchDetail;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
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
    private double totalDistance;   //총거리

    @Column(nullable = false)
    private LocalTime totalTime; // 총 소요시간

    @Column(nullable = false, length = 4)
    private String departurePlaceCode; // 출발지코드

    @Column(nullable = false, length = 50)
    private String departurePlaceName; // 출발지명

    @Column(nullable = false)
    private LocalDateTime departureTime; // 출발 시간   상차 완료 후의 시간, 초기 값은 예상 시간

    @Column(nullable = true)
    private LocalDateTime arrivalTime; // 도착시간   주행 완료 후의 시간

    @Column(nullable = false)
    private double totalWeight; // 총 중량

    @Column(nullable = false)
    private double totalVolume; // 총 볼륨

    @Column(nullable = false)
    private double loadingRate; // 적재율

    @Column(nullable = false)
    private LocalTime breakStartTime; // 휴식 시작 시간

    @Column(nullable = false)
    private LocalTime breakEndTime; // 휴식 종료 시간

    @Column(nullable = false)
    private int restingStopover; // 휴식 경유지 위치 (해당 경유지의 바로 앞)

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus deliveryStatus = DispatchStatus.IN_TRANSIT; // 배차상태

    @Column(columnDefinition = "geometry(LineString)", nullable = false)
    private LineString path;

    @Column(nullable = true, length = 300)
    private String issue; // 이슈

    @ManyToOne(fetch = FetchType.LAZY)
    private DispatchNumber dispatchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sm sm;

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.REMOVE)
    private List<DispatchDetail> dispatchDetailList;

    public void update(IssueRequest request) {
        this.issue = request.issue();
    }

    public void update(
            double totalVolume,
            double totalWeight,
            double totalDistance,
            int totalTime
    ) {
        this.totalVolume = totalVolume;
        this.totalWeight = totalWeight;
        this.totalDistance = totalDistance;
        this.totalTime = LocalTime.of(totalTime / 60, totalTime % 60);
    }

    public void minusOrderCount(int minusOrderCount) {
        if (deliveryOrderCount - minusOrderCount < 0) {
            this.deliveryOrderCount = 0;
        } else {
            this.deliveryOrderCount -= minusOrderCount;
        }
    }

    public void complete() {
        this.deliveryStatus = DispatchStatus.COMPLETED;
    }
}
