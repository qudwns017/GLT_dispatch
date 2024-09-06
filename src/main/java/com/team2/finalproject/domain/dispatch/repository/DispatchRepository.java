package com.team2.finalproject.domain.dispatch.repository;

import com.team2.finalproject.domain.dispatch.exception.DispatchErrorCode;
import com.team2.finalproject.domain.dispatch.exception.DispatchException;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    // DispatchNumber가 가진 Dispatch 리스트 조회
    @Query("SELECT d FROM Dispatch d WHERE d.dispatchNumber IN :dispatchNumbers")
    List<Dispatch> findByDispatchNumbersIn(@Param("dispatchNumbers") List<DispatchNumber> dispatchNumbers);

    default Dispatch findByIdOrThrow(long dispatchId) {
        return findById(dispatchId).orElseThrow(() ->
                new DispatchException(DispatchErrorCode.NOT_FOUND_DISPATCH));
    }

    // id로 Dispatch 조회할 때,
    // Sm과 Users, Vehicle, VehicleDetail
    // DispatchDetailList와 TransportOrder 같이 조회
    @Query("SELECT d FROM Dispatch d " +
            "JOIN FETCH d.sm s " +
            "JOIN FETCH s.users " +
            "JOIN FETCH s.vehicle v " +
            "JOIN FETCH d.dispatchDetailList ddl " +
            "JOIN FETCH ddl.transportOrder " +
            "WHERE d.id = :id")
    Optional<Dispatch> findByIdWithDetails(@Param("id") Long id);

    default Dispatch findByIdWithDetailsOrThrow(Long id) {
        return findByIdWithDetails(id).orElseThrow(() -> new DispatchException(DispatchErrorCode.NOT_FOUND_DISPATCH));
    }
}
