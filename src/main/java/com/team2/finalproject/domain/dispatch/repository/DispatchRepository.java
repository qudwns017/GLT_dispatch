package com.team2.finalproject.domain.dispatch.repository;

import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    // 특정 status를 가진 DispatchNumber가 가진 Dispatch 리스트 조회
    @Query("SELECT d FROM Dispatch d " +
            "JOIN FETCH d.dispatchNumber dn " +
            "WHERE dn IN :dispatchNumbers AND dn.status = :status")
    List<Dispatch> findDispatchesByDispatchNumbersAndStatus(@Param("dispatchNumbers") List<DispatchNumber> dispatchNumbers,
                                                            @Param("status") DispatchNumberStatus status);

    // 특정 status를 가진 DispatchNumber가 가진 Map<DispatchNumber, List<Dispatch>> 조회
    default Map<DispatchNumber, List<Dispatch>> findDispatchMapByDispatchNumbersAndStatus(List<DispatchNumber> dispatchNumbers, DispatchNumberStatus status) {
        List<Dispatch> dispatches = findDispatchesByDispatchNumbersAndStatus(dispatchNumbers, status);
        return dispatches.stream()
                .collect(Collectors.groupingBy(Dispatch::getDispatchNumber));
    }
}
