package com.team2.finalproject.domain.transportorder.repository;

import com.team2.finalproject.domain.transportorder.exception.TransportOrderErrorCode;
import com.team2.finalproject.domain.transportorder.exception.TransportOrderException;
import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransportOrderRepository extends JpaRepository<TransportOrder, Long> {

    @Query("select t from TransportOrder t join fetch t.dispatchDetail d where t.id = :id")
    Optional<TransportOrder> findOrderWithDispatchDetailById(Long id);

    default TransportOrder findOrderWithDispatchDetailByIdOrThrow(long id) {
        return this.findOrderWithDispatchDetailById(id)
            .orElseThrow(()->new TransportOrderException(TransportOrderErrorCode.NOT_FOUND_TRANSPORT_ORDER));
    }
}
