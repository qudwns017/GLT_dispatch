package com.team2.finalproject.domain.sm.repository;

import com.team2.finalproject.domain.sm.exception.SmErrorCode;
import com.team2.finalproject.domain.sm.exception.SmException;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SmRepository extends JpaRepository<Sm, Long> {
    @Query("SELECT s.id FROM Sm s WHERE s.smName = :smName")
    Long findSmIdBySmName(@Param("smName") String smName);

    Optional<Sm> findById(Long smId);

    default Sm findByIdOrThrow(Long smId) {
        return findById(smId)
                .orElseThrow(() -> new SmException(SmErrorCode.NOT_FOUND_SM));
    }
}
