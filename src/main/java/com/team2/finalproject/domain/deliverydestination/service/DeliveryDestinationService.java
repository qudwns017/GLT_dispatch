package com.team2.finalproject.domain.deliverydestination.service;

import com.team2.finalproject.domain.deliverydestination.model.dto.request.DeliveryDestinationRequest;
import com.team2.finalproject.domain.deliverydestination.model.dto.response.DeliveryDestinationResponse;
import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryDestinationService {

    private final DeliveryDestinationRepository deliveryDestinationRepository;

    public DeliveryDestinationResponse getDeliveryDestination(long deliveryDestinationId) {
        DeliveryDestination deliveryDestinationEntity = deliveryDestinationRepository.findByDeliveryDestinationIdOrThrow(
                deliveryDestinationId);
        return DeliveryDestinationResponse.of(deliveryDestinationEntity);
    }

    public DeliveryDestinationResponse addDeliveryDestination(DeliveryDestinationRequest request) {
        DeliveryDestination deliveryDestination = deliveryDestinationRepository.save(DeliveryDestinationRequest.toEntity(request));
        return DeliveryDestinationResponse.of(deliveryDestination);
    }
}
