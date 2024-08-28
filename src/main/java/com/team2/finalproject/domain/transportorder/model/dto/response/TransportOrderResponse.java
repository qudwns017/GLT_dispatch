package com.team2.finalproject.domain.transportorder.model.dto.response;

import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TransportOrderResponse {

    private String transportOrderNumber;
    private String deliveryType;
    private LocalDate requestedWorkDate;
    private LocalTime requestedArrivalTime;
    private LocalTime estimatedWorkTime;
    private String smName;
    private String productName;
    private int productCount;
    private double volume;
    private double weight;
    private String managerName;
    private String phoneNumber;
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
