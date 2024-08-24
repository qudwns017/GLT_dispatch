package com.team2.finalproject.domain.deliverydestination.repository;

import com.team2.finalproject.domain.deliverydestination.exception.DeliveryDestinationErrorCode;
import com.team2.finalproject.domain.deliverydestination.exception.DeliveryDestinationException;
import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface DeliveryDestinationRepository extends JpaRepository<DeliveryDestination, Long> {

    @Query("SELECT d.zipCode, d.id FROM DeliveryDestination d")
    List<Object[]> findAllZipCodesWithIds();

    default Map<String, Integer> findAllZipCodeWithIdsToMap() {
        return findAllZipCodesWithIds().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }

    default DeliveryDestination findByDeliveryDestinationIdOrThrow(long deliveryDestinationId) {
        return findById(deliveryDestinationId).orElseThrow(() ->
                new DeliveryDestinationException(DeliveryDestinationErrorCode.NOT_FOUND_DELIVERY_DESTINATION_ID));
    }
}
