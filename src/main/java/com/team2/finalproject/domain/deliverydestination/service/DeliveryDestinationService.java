package com.team2.finalproject.domain.deliverydestination.service;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.deliverydestination.model.dto.request.DeliveryDestinationRequest;
import com.team2.finalproject.domain.deliverydestination.model.dto.request.UpdateDeliveryDestinationRequest;
import com.team2.finalproject.domain.deliverydestination.model.dto.response.DeliveryDestinationResponse;
import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryDestinationService {

    private final DeliveryDestinationRepository deliveryDestinationRepository;
    private final CenterRepository centerRepository;

    public DeliveryDestinationResponse getDeliveryDestination(long deliveryDestinationId) {
        DeliveryDestination deliveryDestinationEntity = deliveryDestinationRepository.findByIdOrThrow(deliveryDestinationId);
        return DeliveryDestinationResponse.of(deliveryDestinationEntity);
    }

    public DeliveryDestinationResponse addDeliveryDestination(DeliveryDestinationRequest request) {
        Center center = centerRepository.findByCenterByCenterIdOrThrow(request.centerId());
        DeliveryDestination deliveryDestination = DeliveryDestinationRequest.toEntity(request, center);
        DeliveryDestination response = deliveryDestinationRepository.save(deliveryDestination);
        return DeliveryDestinationResponse.of(response);
    }

    public void updateDeliveryDestination(long deliveryDestinationId, UpdateDeliveryDestinationRequest request) {
        DeliveryDestination deliveryDestination = deliveryDestinationRepository.findByDeliveryDestinationIdOrThrow(deliveryDestinationId);
        deliveryDestination.update(request);
        deliveryDestinationRepository.save(deliveryDestination);
    }
}
