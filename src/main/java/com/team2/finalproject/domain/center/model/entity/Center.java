package com.team2.finalproject.domain.center.model.entity;

import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Center extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String centerCode;  // 센터코드

    @Column(nullable = false, length = 50)
    private String centerName;  // 센터명

    @Column(nullable = false, length = 30)
    private String managerName;

    @Column(nullable = false, length = 7)
    private String zipCode;  // 우편번호

    @Column(nullable = false, length = 100)
    private String address;  // 주소

    @Column(nullable = false)
    private Double latitude;  // 위도

    @Column(nullable = false)
    private Double longitude;  // 경도

    @Column(nullable = false, length = 20)
    private String phoneNumber; //전화번호

}
