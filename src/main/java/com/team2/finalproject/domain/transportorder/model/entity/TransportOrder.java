package com.team2.finalproject.domain.transportorder.model.entity;

import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportOrder extends BaseEntity {

    @Column(nullable = false, length = 10)
    private String deliveryType;  // 배송 유형

    @Column(nullable = false, length = 30)
    private String smName; // sm명

    @Column(nullable = false,length = 100)
    private String transportOrderNumber; // 운송장번호

    @Column(nullable = true,length = 100)
    private String orderNumber; // 업체주문번호

    @Column(nullable = true, length = 10)
    private String orderType;  // 주문 유형

    @Column(nullable = false)
    private LocalDate orderDate; // 주문 접수일

    @Column(nullable = false)
    private LocalDate requestedWorkDate;  // 작업희망일

    @Column(nullable = false)
    private LocalTime requestedArrivalTime; // 희망도착시간

    @Column(nullable = false,length = 30)
    private String customerName; // 고객명

    private String customerPhoneNumber; //고객연락처

    @Column(nullable = false,length = 50)
    private String customerAddress; // 주소

    @Column(nullable = false,length = 50)
    private String detailAddress; // 상세 주소

    @Column(nullable = false, length = 7)
    private String zipCode;  // 우편번호

    @Column(nullable = false)
    private double volume;  // 볼륨

    @Column(nullable = false)
    private double weight; // 중량

    @Column(nullable = true, length = 100)
    private String customerNotes;   // 고객 전달 사항

    @Builder.Default
    @Column(nullable = true)
    private LocalTime estimatedWorkTime = LocalTime.of(0,1);  // 예상작업시간

    @Column(nullable = false, length = 20)
    private String deliveryDestinationCode; // 배송처코드

    @Column(nullable = false, length = 100)
    private String productName; // 상품명

    @Column(nullable = true, length = 100)
    private String productCode; // 상품코드

    @Column(nullable = false)
    private int productCount; // 아이템수량


    @Column(nullable = false,length = 4)
    private String centerId; // 센터 id

    @Builder.Default
    @Column(nullable = false)
    private boolean isPending = false; // 보류여부
}
