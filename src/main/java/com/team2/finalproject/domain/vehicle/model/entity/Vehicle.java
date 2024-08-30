package com.team2.finalproject.domain.vehicle.model.entity;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.vehicledetail.model.entity.VehicleDetail;
import com.team2.finalproject.domain.vehicledetail.model.type.VehicleType;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Vehicle extends BaseEntity {

    @Column(nullable = false)
    private String vehicleNumber;  // 차량 번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType; // 차종

    @Column(nullable = false)
    private boolean ownershipType; // 지입/용차 (True for 지입, False for 용차)

    @Column(nullable = false)
    private double length; // 길이

    @Column(nullable = false)
    private double fuelEfficiency; // 연비

    @Column(nullable = false)
    private double width; // 폭

    @Column(nullable = false)
    private double height; // 높이

    @Column(nullable = false)
    private double maxLoadWeight; // 최대 적재 중량

    @Column(nullable = false)
    private double maxLoadVolume; // 최대 적재 부피

    @Column(nullable = false)
    private int manufactureYear; // 연식

    @Column(nullable = false)
    private double usableLength; // 실사용 길이

    @Column(nullable = false)
    private double usableWidth; // 실사용 폭

    @Column(nullable = false)
    private double usableHeight; // 실사용 높이

    @Column(nullable = false)
    private String createdBy; // 생성한 사용자

    @Column(nullable = false)
    private String updatedBy; // 수정한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    private VehicleDetail vehicleDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    private Center center;

    @OneToOne
    private Sm sm;
}
