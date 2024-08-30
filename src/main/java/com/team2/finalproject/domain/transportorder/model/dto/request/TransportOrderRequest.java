package com.team2.finalproject.domain.transportorder.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record TransportOrderRequest(
        @Schema(example = "2024-08-30T09:00:00", description = "상차 시작 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime loadingStartTime,

        @Schema(example = "배차1", description = "배차 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        String dispatchName,

        @Schema(
                description = "주문 목록",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = """
            [
                {
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
                    "address": "서울특별시 강남구 테헤란로 123",
                    "detailAddress": "아파트 101호",
                    "zipcode": "06101",
                    "volume": 2.5,
                    "weight": 10.0,
                    "note": "문 앞에 놔주세요",
                    "expectedServiceDuration": 30,
                    "productName": "전자제품",
                    "productCode": "P123456",
                    "productQuantity": 5
                },
                {
                    "deliveryType": "지입",
                    "smId": 124,
                    "smName": "이영희",
                    "shipmentNum": "0987654321",
                    "clientOrderKey": "B987654321",
                    "orderType": "수거",
                    "receivedDate": "2024-06-01",
                    "serviceRequestDate": "2024-06-03",
                    "serviceRequestTime": "10:00",
                    "clientName": "박영수",
                    "contact": "010-9876-5432",
                    "address": "서울특별시 서초구 반포대로 200",
                    "detailAddress": "빌라 203호",
                    "zipcode": "06500",
                    "volume": 3.0,
                    "weight": 15.0,
                    "note": "빠른 수거 부탁드립니다",
                    "expectedServiceDuration": 20,
                    "productName": "가전제품",
                    "productCode": "G987654",
                    "productQuantity": 2
                }
            ]
            """
        )
        List<OrderRequest> orderReuquestList
) {}
