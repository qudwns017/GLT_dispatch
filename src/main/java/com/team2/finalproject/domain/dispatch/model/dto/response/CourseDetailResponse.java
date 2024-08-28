package com.team2.finalproject.domain.dispatch.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailResponse {
    private boolean errorYn;  // 진입 조건
    private int ett;  // 예상 이동 시간 (분)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expectationOperationStartTime;  // 예상 작업 시작 시간

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expectationOperationEndTime;  // 예상 작업 종료 시간

    private long deliveryDestinationId;  // 배송처 ID (배송처 코드)
    private String managerName;  // 담당자 이름
    private String phoneNumber;  // 담당자 전화번호
    private double lat;  // 경유지 위도
    private double lon;  // 경유지 경도
    private double distance;  // 이동 거리 (km)
    private String deliveryType;  // 배송유형 ("지입", "용차", "택배")
    private long smId;  // 기사 ID
    private String smName;  // 기사 이름
    private String shipmentNum;  // 운송장 번호
    private String clientOrderKey;  // 업체 주문 번호
    private String orderType;  // 주문 유형 ("배송", "수거")

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate receivedDate;  // 주문 접수일 (YYYYMMDD)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate serviceRequestDate;  // 작업 희망일 (YYYYMMDD)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime serviceRequestTime;  // 희망 도착 시간 (HH:MM:SS)

    private String clientName;  // 고객 이름
    private String contact;  // 고객 연락처
    private String address;  // 주소
    private String detailAddress;  // 상세 주소
    private String zipcode;  // 우편번호
    private double volume;  // 볼륨
    private double weight;  // 중량
    private String note;  // 고객 전달 사항
    private int expectedServiceDuration;  // 예상 작업 시간 (분)
    private String productName;  // 상품명
    private String productCode;  // 상품 코드
    private int productQuantity;  // 상품 수량
}
