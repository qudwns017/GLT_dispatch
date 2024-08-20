package com.team2.finalproject.domain.transportorder.service;

import com.team2.finalproject.domain.deliverydestination.repository.DeliveryDestinationRepository;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.transportorder.model.dto.request.SmNameAndPostalCodeRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndPostalCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransportOrderService {

    private final SmRepository smRepository;

    private final DeliveryDestinationRepository deliveryDestinationRepository;

    public List<SmNameAndPostalCodeResponse> validateSmNameAndPostalCodes(List<SmNameAndPostalCodeRequest> requests) {
        Map<String, Integer> smNameWithIdMap = smRepository.findAllSmNameWithIdsToMap();
        Map<String, Integer> postalWithIdMap = deliveryDestinationRepository.findAllPostalCodeWithIdsToMap();

        return requests.stream()
                .map(request -> SmNameAndPostalCodeResponse.builder()
                        .postalCodeValid(postalWithIdMap.containsKey(request.postalCode()))
                        .deliveryDestinationId(postalWithIdMap.getOrDefault(request.postalCode(), 0))
                        .smNameValid(smNameWithIdMap.containsKey(request.smName()))
                        .smId(smNameWithIdMap.getOrDefault(request.smName(), 0))
                        .build())
                .toList();
    }
}
