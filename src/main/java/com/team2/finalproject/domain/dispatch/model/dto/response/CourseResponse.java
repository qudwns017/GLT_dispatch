package com.team2.finalproject.domain.dispatch.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    @Schema(description = "전체 주문의 수 or 거리 ", example = "20")
    private int totalOrderOrDistanceNum;

    @Schema(description = "가용주문 수 or 거리", example = "80")
    private int availableNum;

    @Schema(description = "오류 여부", example = "false")
    private boolean errorYn;

    @Schema(description = "기사 ID", example = "123")
    private Long smId;

    @Schema(description = "기사 이름", example = "홍길동")
    private String smName;

    @Schema(description = "기사 전화번호", example = "010-1234-5678")
    private String smPhoneNumber;

    @Schema(description = "차량 종류", example = "WING_BODY")
    private String vehicleType;

    @Schema(description = "차량 톤", example = "2.5")
    private double vehicleTon;

    @Schema(description = "주문 수", example = "10")
    private int orderNum;

    @Schema(description = "주행 거리 (km)", example = "150")
    private int mileage;

    @Schema(description = "주행 시간 (분)", example = "120")
    private int totalTime;

    @Schema(description = "용적률", example = "75")
    private int floorAreaRatio;

    @Schema(description = "휴식 시작 시간", example = "11:00:00")
    private LocalTime breakStartTime;

    @Schema(description = "휴식 종료 시간", example = "12:00:00")
    private LocalTime breakEndTime;

    @Schema(description = "휴식 경유지 위치", example = "2")
    private int restingPosition;

    @Schema(description = "경로의 상세 정보 리스트")
    private List<CourseDetailResponse> courseDetailResponseList;

    @Schema(description = "경로의 좌표 리스트")
    private List<CoordinatesResponse> coordinatesResponseList;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseDetailResponse {
        @Schema(description = "톤코드 오류 여부", example = "false")
        private boolean restrictedTonCode;

        @Schema(description = "요청 시간 지연 여부", example = "false")
        private boolean delayRequestTime;

        @Schema(description = "계약 초과 위반 여부", example = "false")
        private boolean overContractNum;

        @Schema(description = "예상 이동 시간 (분)", example = "30")
        private int ett;

        @Schema(description = "예상 작업 시작 시간", example = "2024-08-30T09:30:00")
        private LocalDateTime expectationOperationStartTime;

        @Schema(description = "예상 작업 종료 시간", example = "2024-08-30T10:00:00")
        private LocalDateTime expectationOperationEndTime;

        @Schema(description = "배송처 ID", example = "456")
        private long deliveryDestinationId;

        @Schema(description = "담당자 이름", example = "이영희")
        private String managerName;

        @Schema(description = "담당자 전화번호", example = "010-9876-5432")
        private String phoneNumber;

        @Schema(description = "경유지 위도", example = "37.5665")
        private double lat;

        @Schema(description = "경유지 경도", example = "126.9780")
        private double lon;

        @Schema(description = "이동 거리 (km)", example = "20")
        private double distance;

        @Schema(description = "배송유형", example = "택배")
        private String deliveryType;

        @Schema(description = "기사 ID", example = "123")
        private long smId;

        @Schema(description = "기사 이름", example = "홍길동")
        private String smName;

        @Schema(description = "운송장 번호", example = "1234567890")
        private String shipmentNumber;

        @Schema(description = "업체 주문 번호", example = "A123456789")
        private String clientOrderKey;

        @Schema(description = "주문 유형", example = "배송")
        private String orderType;

        @Schema(description = "주문 접수일", example = "2024-05-01")
        private LocalDate receivedDate;

        @Schema(description = "작업 희망일", example = "2024-05-02")
        private LocalDate serviceRequestDate;

        @Schema(description = "희망 도착 시간", example = "14:00")
        private LocalTime serviceRequestTime;

        @Schema(description = "고객 이름", example = "김철수")
        private String clientName;

        @Schema(description = "고객 연락처", example = "010-1234-5678")
        private String contact;

        @Schema(description = "도로명 주소", example = "서울특별시 강남구 테헤란로 123")
        private String roadAddress;

        @Schema(description = "지번 주소", example = "서울특별시 강남구 대치동")
        private String lotNumberAddress;

        @Schema(description = "상세 주소", example = "아파트 101호")
        private String detailAddress;

        @Schema(description = "우편번호", example = "06101")
        private String zipcode;

        @Schema(description = "볼륨", example = "2.5")
        private double volume;

        @Schema(description = "중량", example = "10.0")
        private double weight;

        @Schema(description = "고객 전달 사항", example = "문 앞에 놔주세요")
        private String note;

        @Schema(description = "예상 작업 시간 (분)", example = "30")
        private int expectedServiceDuration;

        @Schema(description = "상품명", example = "전자제품")
        private String productName;

        @Schema(description = "상품 코드", example = "P123456")
        private String productCode;

        @Schema(description = "상품 수량", example = "5")
        private int productQuantity;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoordinatesResponse {
        @Schema(description = "경도", example = "126.9780")
        private double lon;

        @Schema(description = "위도", example = "37.5665")
        private double lat;
    }
}
