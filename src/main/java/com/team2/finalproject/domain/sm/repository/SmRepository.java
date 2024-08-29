package com.team2.finalproject.domain.sm.repository;

import com.team2.finalproject.domain.sm.exception.SmErrorCode;
import com.team2.finalproject.domain.sm.exception.SmException;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface SmRepository extends JpaRepository<Sm, Long> {

    @Query("SELECT s.smName, s.id FROM Sm s")
    List<Object[]> findAllSmNamesWithIds();

    default Map<String, Integer> findAllSmNameWithIdsToMap() {
        return findAllSmNamesWithIds().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }

    Optional<Sm> findById(Long smId);

    default Sm findByIdOrThrow(Long smId) {
        return findById(smId)
                .orElseThrow(() -> new SmException(SmErrorCode.NOT_FOUND_SM));
    }
}
