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

    @Column(nullable = false, length = 100)
    private String destinationName; // 배송처명

    @Column(nullable = false, length = 50)
    private String address;  // 기본주소

    @Column(nullable = false, length = 50)
    private String detailAddress;  // 상세주소

    @Column(nullable = false, length = 7)
    private String zipCode;  // 우편번호

    @Column(nullable = false, length = 30)
    private String managerName;  // 담당자명

    @Column(nullable = false, length = 20)
    private String phoneNumber;  // 전화번호

    @Column(nullable = false)
    private Double latitude;  // 위도

    @Column(nullable = false)
    private Double longitude;  // 경도

    @Column(nullable = true, length = 10)
    private String restrictedTonCode;    // 진입 불가 톤 코드

    @Column(nullable = true, length = 100)
    private String comment; // 비고

    @Builder.Default
    @Column(nullable = false)
    private int delayTime = 0; // 작업 추가 소요시간(분)

    @ManyToOne(fetch = FetchType.LAZY)
    private Center center;
}
