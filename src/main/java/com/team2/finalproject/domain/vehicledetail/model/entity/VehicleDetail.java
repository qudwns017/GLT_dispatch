package com.team2.finalproject.domain.vehicledetail.model.entity;

import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDetail extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String vehicleCode;  // 차량 톤코드

    @Column(nullable = false, length = 50)
    private String vehicleName;  // 차량명

    @Column(nullable = false, length = 50)
    private String vehicleType;  // 차량종류

    @Column(nullable = false)
    private Long centerId;
}
