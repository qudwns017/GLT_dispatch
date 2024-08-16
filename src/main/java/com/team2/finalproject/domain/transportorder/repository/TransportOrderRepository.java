package com.team2.finalproject.domain.transportorder.repository;

import com.team2.finalproject.domain.transportorder.model.entity.TransportOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportOrderRepository extends JpaRepository<TransportOrder, Long> {

}
