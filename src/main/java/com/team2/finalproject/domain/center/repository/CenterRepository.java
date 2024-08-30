package com.team2.finalproject.domain.center.repository;

import com.team2.finalproject.domain.center.exception.CenterErrorCode;
import com.team2.finalproject.domain.center.exception.CenterException;
import com.team2.finalproject.domain.center.model.entity.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long> {

    default Center findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() ->
                new CenterException(CenterErrorCode.NOT_FOUND_CENTER));
    }

    Optional<Center> findByCenterCode(String centerCode);

    default Center findByCenterCodeOrThrow(String centerCode) {
        return findByCenterCode(centerCode).orElseThrow(() -> new CenterException(CenterErrorCode.NOT_FOUND_CENTER));
    }

    @Query("SELECT c.comment FROM Center c WHERE c.id = :id")
    Optional<String> findCommentById(@Param("id") Long id);

    default String findCommentByIdOrThrow(Long id) {
        return findCommentById(id).orElseThrow(() -> new CenterException(CenterErrorCode.NOT_FOUND_CENTER));
    }
}
