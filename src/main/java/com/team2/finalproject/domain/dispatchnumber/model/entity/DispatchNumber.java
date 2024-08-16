package com.team2.finalproject.domain.dispatchnumber.model.entity;

import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DispatchNumber extends BaseEntity {

    @Column(nullable = false)
    private Long centerId; // 센터코드

    @Column(nullable = false)
    private Long driverId; // 담당자Id

    @Column(nullable = false, length = 20)
    private String dispatchNumber; // 배차번호

    @Column(nullable = false, length = 50)
    private String dispatchType; // 배차명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchNumberStatus status = DispatchNumberStatus.WAITING;; // 상태

}
