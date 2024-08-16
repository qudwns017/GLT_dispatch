package com.team2.finalproject.domain.deliverydestination.model.entity;

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
public class DeliveryDestination extends BaseEntity {

    @Column(nullable = false)
    private Long centerId;

    @Column(nullable = false, length = 100)
    private String destinationName; // 배송처명

    @Column(nullable = false, length = 50)
    private String basicAddress;  // 기본주소

    @Column(nullable = false, length = 50)
    private String detailedAddress;  // 상세주소

    @Column(nullable = false, length = 7)
    private String postalCode;  // 우편번호

    @Column(nullable = false, length = 30)
    private String managerName;

    @Column(nullable = false, length = 20)
    private String managerPhoneNumber;

    @Column(nullable = false, length = 30)
    private String businessNumber;  // 사업자번호

    @Column(nullable = false)
    private Double latitude;  // 위도

    @Column(nullable = false)
    private Double longitude;  // 경도

    @Column(nullable = true, length = 10)
    private String restrictedTonCode;    // 진입 불가 톤 코드
}
