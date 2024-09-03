package com.team2.finalproject.domain.transportorder.model.dto.response;

import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransportOrderResponse {

    @Schema(example = "20240808274985", description = "운송장 번호")
    private String shipmentNumber;
    @Schema(example = "지입", description = "배송 유형")
    private String deliveryType;
    @Schema(example = "2024-08-19", description = "작업희망일")
    private LocalDate requestedWorkDate;
    @Schema(example = "14:00", description = "희망도착시간")
    private LocalTime requestedArrivalTime;
    @Schema(example = "00:30", description = "예상작업 소요시간")
    private LocalTime estimatedWorkTime;
    @Schema(example = "홍길동", description = "기사명")
    private String smName;
    @Schema(example = "사과", description = "상품명")
    private String productName;
    @Schema(example = "4", description = "상품 수량")
    private int productCount;
    @Schema(example = "80.5", description = "볼륨")
    private double volume;
    @Schema(example = "80.1", description = "중량")
    private double weight;
    private DestinationInfo destinationInfo;
    private ClientInfo clientInfo;

    private TransportOrderResponse(String shipmentNumber,String deliveryType,LocalDate requestedWorkDate, LocalTime requestedArrivalTime,LocalTime estimatedWorkTime, String smName,String productName, int productCount, double volume, double weight,DestinationInfo destinationInfo,ClientInfo clientInfo){
        this.shipmentNumber = shipmentNumber;
        this.deliveryType = deliveryType;
        this.requestedWorkDate = requestedWorkDate;
        this.requestedArrivalTime = requestedArrivalTime;
        this.estimatedWorkTime = estimatedWorkTime;
        this.smName = smName;
        this.productName = productName;
        this.productCount = productCount;
        this.volume = volume;
        this.weight = weight;
        this.destinationInfo = destinationInfo;
        this.clientInfo = clientInfo;
    }

    public static TransportOrderResponse of(TransportOrder transportOrder,String managerName, String phoneNumber, Long deliveryDestinationCode){
        return new TransportOrderResponse(
            transportOrder.getShipmentNumber(),
            transportOrder.getDeliveryType(),
            transportOrder.getRequestedWorkDate(),
            transportOrder.getRequestedArrivalTime(),
            transportOrder.getEstimatedWorkTime(),
            transportOrder.getSmName(),
            transportOrder.getProductName(),
            transportOrder.getProductCount(),
            transportOrder.getVolume(),
            transportOrder.getWeight(),
            DestinationInfo.of(managerName, phoneNumber, deliveryDestinationCode),
            null
        );
    }
    public static TransportOrderResponse of(TransportOrder transportOrder,String clientName, String phoneNumber, String roadAddress, String detailAddress, String note){
        return new TransportOrderResponse(
            transportOrder.getShipmentNumber(),
            transportOrder.getDeliveryType(),
            transportOrder.getRequestedWorkDate(),
            transportOrder.getRequestedArrivalTime(),
            transportOrder.getEstimatedWorkTime(),
            transportOrder.getSmName(),
            transportOrder.getProductName(),
            transportOrder.getProductCount(),
            transportOrder.getVolume(),
            transportOrder.getWeight(),
            null,
            ClientInfo.of(clientName, phoneNumber, roadAddress, detailAddress, note)
        );
    }

    @Getter
    @NoArgsConstructor
    public static class DestinationInfo{
        @Schema(example = "유관순", description = "담당자명")
        private String managerName;
        @Schema(example = "010-1111-2222", description = "담당자 연락처")
        private String phoneNumber;
        @Schema(example = "4", description = "배송처 코드")
        private Long deliveryDestinationCode;

        private DestinationInfo(String managerName, String phoneNumber, Long deliveryDestinationCode){
            this.managerName = managerName;
            this.phoneNumber = phoneNumber;
            this.deliveryDestinationCode = deliveryDestinationCode;
        }

        private static DestinationInfo of(String managerName, String phoneNumber, Long deliveryDestinationCode){
            return new DestinationInfo(managerName, phoneNumber, deliveryDestinationCode);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ClientInfo{
        @Schema(example = "홍길동", description = "고객명")
        private String clientName;
        @Schema(example = "01012345678", description = "연락처")
        private String phoneNumber;
        @Schema(example = "충남 논산시 중앙대로 374번길 41-11", description = "도로명 주소")
        private String roadAddress;
        @Schema(example = "1층 물류센터", description = "상세주소")
        private String detailAddress;
        @Schema(example = "조심히 다뤄주세요.", description = "고객 전달 사항")
        private String note;

        private ClientInfo(String clientName, String phoneNumber, String roadAddress, String detailAddress, String note){
            this.clientName = clientName;
            this.phoneNumber = phoneNumber;
            this.roadAddress = roadAddress;
            this.detailAddress = detailAddress;
            this.note = note;
        }

        private static ClientInfo of(String clientName, String phoneNumber, String roadAddress, String detailAddress, String note){
            return new ClientInfo(clientName, phoneNumber, roadAddress, detailAddress, note);
        }
    }


}
