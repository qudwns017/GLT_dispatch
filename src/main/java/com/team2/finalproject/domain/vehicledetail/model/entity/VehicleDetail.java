package com.team2.finalproject.domain.vehicledetail.model.entity;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class VehicleDetail extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String vehicleCode;  // 차량 톤코드

    @Column(nullable = false, length = 50)
    private double vehicleTon;  // 차량 톤

    @Column(nullable = false, length = 50)
    private String vehicleType;  // 차량종류

    @ManyToOne(fetch = FetchType.LAZY)
    private Center center;

    @OneToMany(mappedBy = "vehicleDetail")
    private List<Vehicle> vehicle;
}
