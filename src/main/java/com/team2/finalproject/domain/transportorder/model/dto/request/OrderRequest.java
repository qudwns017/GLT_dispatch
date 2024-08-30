package com.team2.finalproject.domain.transportorder.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record OrderRequest(
        @Schema(example = "택배", description = "배송유형: '지입', '택배'", requiredMode = Schema.RequiredMode.REQUIRED)
        String deliveryType,

        @Schema(example = "123", description = "기사 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        Long smId,

        @Schema(example = "홍길동", description = "기사 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        String smName,

        @Schema(example = "1234567890", description = "운송장 번호", requiredMode = Schema.RequiredMode.REQUIRED)
        String shipmentNumber,

        @Schema(example = "A123456789", description = "업체 주문 번호")
        String clientOrderKey,

        @Schema(example = "배송", description = "주문유형: '배송', '수거'", requiredMode = Schema.RequiredMode.REQUIRED)
        String orderType,

        @Schema(example = "2024-05-01", description = "주문 접수일", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate receivedDate,

        @Schema(example = "2024-05-02", description = "작업 희망일", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate serviceRequestDate,

        @Schema(example = "14:00", description = "희망 도착 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime serviceRequestTime,

        @Schema(example = "김철수", description = "고객명", requiredMode = Schema.RequiredMode.REQUIRED)
        String clientName,

        @Schema(example = "010-1234-5678", description = "고객 연락처", requiredMode = Schema.RequiredMode.REQUIRED)
        String contact,

        @Schema(example = "서울특별시 강남구 테헤란로 123", description = "주소", requiredMode = Schema.RequiredMode.REQUIRED)
        String address,

        @Schema(example = "아파트 101호", description = "상세 주소", requiredMode = Schema.RequiredMode.REQUIRED)
        String detailAddress,

        @Schema(example = "06101", description = "우편번호", requiredMode = Schema.RequiredMode.REQUIRED)
        String zipcode,

        @Schema(example = "2.5", description = "볼륨 (단위: m³)", requiredMode = Schema.RequiredMode.REQUIRED)
        Double volume,

        @Schema(example = "10.0", description = "중량 (단위: kg)", requiredMode = Schema.RequiredMode.REQUIRED)
        Double weight,

        @Schema(example = "문 앞에 놔주세요", description = "고객 전달 사항")
        String note,

        @Schema(example = "30", description = "예상 작업 시간 (단위: 분)")
        Integer expectedServiceDuration,

        @Schema(example = "전자제품", description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
        String productName,

        @Schema(example = "P123456", description = "상품 코드")
        String productCode,

        @Schema(example = "5", description = "상품 수량", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer productQuantity
) {}