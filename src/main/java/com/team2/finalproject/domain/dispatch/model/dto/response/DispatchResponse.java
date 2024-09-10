package com.team2.finalproject.domain.dispatch.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResponse {
    @Schema(description = "배차코드 = 배차번호", example = "D123456789")
    private String dispatchCode;

    @Schema(description = "배차명", example = "배차1")
    private String dispatchName;

    @Schema(description = "총 주문", example = "100")
    private int totalOrder;

    @Schema(description = "오류주문 수", example = "5")
    private int totalErrorNum;

    @Schema(description = "총 예상시간 (분)", example = "480")
    private int totalTime;

    @Schema(description = "총 용적률", example = "85")
    private int totalFloorAreaRatio;

    @Schema(description = "상차 시작 시간", example = "2024-08-30T09:00:00")
    private LocalDateTime loadingStartTime;

    @Schema(description = "배송 유형", example = "지입")
    private String contractType;

    @Schema(
            description = "시작 경유지 정보",
            example = """
        {
            "centerId": 123,
            "roadAddress": "서울시 강동구 천호대로 1107"
            "lotNumberAddress": "서울시 강동구 천호동",
            'detailAddress": "1층 물류센터",
            "lat": 37.5409,
            "lon": 127.1263,
            "expectedServiceDuration": "01:00",
            "departureTime": "2024-08-30T10:00:00"
        }
        """
    )
    private StartStopoverResponse startStopoverResponse;

    @Schema(
            description = "경로별 리스트",
            example = """
        [
            {
                "errorYn": false,
                "smName": "홍길동",
                "smPhoneNumber": "010-1234-5678",
                "tonCode": "5T",
                "ton": 5.0,
                "orderNum": 10,
                "mileage": 150,
                "totalTime": 120,
                "floorAreaRatio": 75,
                "breakStartTime": "11:00:00",
                "breakEndTime": "13:00:00",
                "restingPosition": 2,
                "courseDetailResponseList": [
                    {
                        "errorYn": false,
                        "ett": 30,
                        "expectationOperationStartTime": "2024-08-30T09:30:00",
                        "expectationOperationEndTime": "2024-08-30T10:00:00",
                        "deliveryDestinationId": 456,
                        "managerName": "이영희",
                        "phoneNumber": "010-9876-5432",
                        "lat": 37.5665,
                        "lon": 126.9780,
                        "distance": 20.0,
                        "deliveryType": "택배",
                        "smId": 123,
                        "smName": "홍길동",
                        "shipmentNum": "1234567890",
                        "clientOrderKey": "A123456789",
                        "orderType": "배송",
                        "receivedDate": "2024-05-01",
                        "serviceRequestDate": "2024-05-02",
                        "serviceRequestTime": "14:00",
                        "clientName": "김철수",
                        "contact": "010-1234-5678",
                        "roadAddress": "서울특별시 강남구 테헤란로 123",
                        "lotNumberAddress": "서울특별시 강남구 대치동",
                        "detailAddress": "아파트 101호",
                        "zipcode": "06101",
                        "volume": 2.5,
                        "weight": 10.0,
                        "note": "문 앞에 놔주세요",
                        "expectedServiceDuration": 30,
                        "productName": "전자제품",
                        "productCode": "P123456",
                        "productQuantity": 5
                    }
                ],
                "coordinatesResponseList": [
                    {
                        "lon": 126.9780,
                        "lat": 37.5665
                    },
                    {
                        "lon": 126.9876,
                        "lat": 37.5653
                    }
                ]
            }
        ]
        """
    )
    private List<CourseResponse> course;
}
