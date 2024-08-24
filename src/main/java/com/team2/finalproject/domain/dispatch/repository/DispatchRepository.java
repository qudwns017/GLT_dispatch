package com.team2.finalproject.domain.dispatch.repository;

import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    // DispatchNumber가 가진 Dispatch 리스트 조회
    @Query("SELECT d FROM Dispatch d WHERE d.dispatchNumber IN :dispatchNumbers")
    List<Dispatch> findByDispatchNumbersIn(@Param("dispatchNumbers") List<DispatchNumber> dispatchNumbers);

    default Map<DispatchNumber, List<Dispatch>> findDispatchMapByDispatchNumbers(List<DispatchNumber> dispatchNumbers) {
        List<Dispatch> dispatches = findByDispatchNumbersIn(dispatchNumbers);
        return dispatches.stream()
                .collect(Collectors.groupingBy(Dispatch::getDispatchNumber));
    }
}
