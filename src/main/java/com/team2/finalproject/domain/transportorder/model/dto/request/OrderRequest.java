package com.team2.finalproject.domain.transportorder.model.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record OrderRequest(
        String deliveryType, // 배송유형 "지입","용차","택배"
        Long smId, // 기사 ID
        String smName, // 기사 이름
        String shipmentNumber, // 운송장 번호
        String clientOrderKey, // 업체 주문 번호
        String orderType, // 주문유형 "배송","수거"
        LocalDate receivedDate, // 주문 접수일
        LocalDate serviceRequestDate, // 작업 희망일
        LocalTime serviceRequestTime, // 희망 도착 시간
        String clientName, // 고객명
        String contact, // 고객 연락처
        String address, // 주소
        String detailAddress, // 상세 주소
        String zipcode, // 우편번호
        Double volume, // 볼륨
        Double weight, // 중량
        String note, // 고객 전달 사항
        Integer expectedServiceDuration, // 예상 작업 시간
        String productName, // 상품명
        String productCode, // 상품 코드
        Integer productQuantity // 상품 수량
) {}