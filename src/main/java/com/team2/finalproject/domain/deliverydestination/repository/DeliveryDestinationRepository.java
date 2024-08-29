package com.team2.finalproject.domain.deliverydestination.repository;

import com.team2.finalproject.domain.deliverydestination.exception.DeliveryDestinationErrorCode;
import com.team2.finalproject.domain.deliverydestination.exception.DeliveryDestinationException;
import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    default DeliveryDestination findByIdOrThrow(Long id){
        return findById(id).orElseThrow(
            () -> new DeliveryDestinationException(DeliveryDestinationErrorCode.NOT_FOUND_DELIVERY_DESTINATION)
        );
    }

    Optional<DeliveryDestination> findByRoadAddressAndDetailAddress(String roadAddress, String detailAddress);

    default DeliveryDestination findByFullAddress(String roadAddress, String detailAddress) {
        return findByRoadAddressAndDetailAddress(roadAddress, detailAddress).orElse(null);
    }


    @Query("SELECT d.comment FROM DeliveryDestination d WHERE d.id = :id")
    Optional<String> findCommentById(@Param("id") Long id);

    default String findCommentByIdOrThrow(Long id) {
        return findCommentById(id).orElseThrow(() -> new DeliveryDestinationException(DeliveryDestinationErrorCode.NOT_FOUND_DELIVERY_DESTINATION));
    }
}
