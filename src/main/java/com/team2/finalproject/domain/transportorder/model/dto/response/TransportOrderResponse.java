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
    private String transportOrderNumber;
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
    @Schema(example = "80.1", description = "즁량")
    private double weight;
    @Schema(example = "유관순", description = "담당자명")
    private String managerName;
    @Schema(example = "010-1111-2222", description = "담당자 연락처")
    private String phoneNumber;
    @Schema(example = "4", description = "배송처 코드")
    private Long deliveryDestinationCode;

    private TransportOrderResponse(String transportOrderNumber,String deliveryType,LocalDate requestedWorkDate, LocalTime requestedArrivalTime,LocalTime estimatedWorkTime, String smName,String productName, int productCount, double volume, double weight, String managerName,String phoneNumber, Long deliveryDestinationCode){
        this.transportOrderNumber = transportOrderNumber;
        this.deliveryType = deliveryType;
        this.requestedWorkDate = requestedWorkDate;
        this.requestedArrivalTime = requestedArrivalTime;
        this.estimatedWorkTime = estimatedWorkTime;
        this.smName = smName;
        this.productName = productName;
        this.productCount = productCount;
        this.volume = volume;
        this.weight = weight;
        this.managerName = managerName;
        this.phoneNumber = phoneNumber;
        this.deliveryDestinationCode = deliveryDestinationCode;
    }
    private TransportOrderResponse(String transportOrderNumber,String deliveryType,LocalDate requestedWorkDate, LocalTime requestedArrivalTime,LocalTime estimatedWorkTime, String smName,String productName, int productCount, double volume, double weight){
        this.transportOrderNumber = transportOrderNumber;
        this.deliveryType = deliveryType;
        this.requestedWorkDate = requestedWorkDate;
        this.requestedArrivalTime = requestedArrivalTime;
        this.estimatedWorkTime = estimatedWorkTime;
        this.smName = smName;
        this.productName = productName;
        this.productCount = productCount;
        this.volume = volume;
        this.weight = weight;
    }

    public static TransportOrderResponse of(TransportOrder transportOrder,String managerName,String phoneNumber, Long deliveryDestinationCode){
        return new TransportOrderResponse(
            transportOrder.getTransportOrderNumber(),
            transportOrder.getDeliveryType(),
            transportOrder.getRequestedWorkDate(),
            transportOrder.getRequestedArrivalTime(),
            transportOrder.getEstimatedWorkTime(),
            transportOrder.getSmName(),
            transportOrder.getProductName(),
            transportOrder.getProductCount(),
            transportOrder.getVolume(),
            transportOrder.getWeight(),
            managerName,
            phoneNumber,
            deliveryDestinationCode
        );
    }
    public static TransportOrderResponse of(TransportOrder transportOrder){
        return new TransportOrderResponse(
            transportOrder.getTransportOrderNumber(),
            transportOrder.getDeliveryType(),
            transportOrder.getRequestedWorkDate(),
            transportOrder.getRequestedArrivalTime(),
            transportOrder.getEstimatedWorkTime(),
            transportOrder.getSmName(),
            transportOrder.getProductName(),
            transportOrder.getProductCount(),
            transportOrder.getVolume(),
            transportOrder.getWeight()
        );
    }
}
