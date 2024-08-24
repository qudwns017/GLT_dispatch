package com.team2.finalproject.domain.center.repository;

import com.team2.finalproject.domain.center.exception.CenterErrorCode;
import com.team2.finalproject.domain.center.exception.CenterException;
import com.team2.finalproject.domain.center.model.entity.Center;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CenterRepository extends JpaRepository<Center, Long> {

    default Center findByCenterByCenterIdOrThrow(long id) {
        return findById(id).orElseThrow(() ->
                new CenterException(CenterErrorCode.NOT_FOUND_CENTER));
    }

    Optional<Center> findByCenterCode(String centerCode);

    default Center findByCenterCodeWithThrow(String centerCode){
        return findByCenterCode(centerCode).orElseThrow(()->new CenterException(CenterErrorCode.NOT_FOUND_CENTER));
    }
}
