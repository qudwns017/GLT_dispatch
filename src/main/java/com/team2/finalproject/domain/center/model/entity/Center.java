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
    private String field;  // 필드

    @Column(nullable = false, length = 50)
    private String centerName;  // 센터명

    @Column(nullable = false, length = 7)
    private String postalCode;  // 우편번호

    @Column(nullable = false, length = 100)
    private String address;  // 주소

    @Column(nullable = false)
    private Double latitude;  // 위도

    @Column(nullable = false)
    private Double longitude;  // 경도

}
