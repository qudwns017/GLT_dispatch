package com.team2.finalproject.domain.deliverydestination.model.entity;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDestination extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String field;  // 필드

    @Column(nullable = false, length = 50)
    private String basicAddress;  // 기본주소

    @Column(nullable = false, length = 50)
    private String detailedAddress;  // 상세주소

    @Column(nullable = false, length = 7)
    private String postalCode;  // 우편번호

    @Column(nullable = false, length = 30)
    private String businessNumber;  // 사업자번호

    @Column(nullable = false)
    private Double latitude;  // 위도

    @Column(nullable = false)
    private Double longitude;  // 경도

    @ManyToOne(fetch = FetchType.LAZY)
    private Center center;

    //배송처명

    //담당자명

    //담당자 전화번호

    //진입 불가 톤코드
}
