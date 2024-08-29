package com.team2.finalproject.domain.dispatchnumber.repository;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatchnumber.exception.DispatchNumberErrorCode;
import com.team2.finalproject.domain.dispatchnumber.exception.DispatchNumberException;
import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import com.team2.finalproject.domain.users.model.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DispatchNumberRepository extends JpaRepository<DispatchNumber, Long> {

    // 1. 센터 코드를 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.center = :center " +
            "AND d.loadingStartTime " +
            "BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterAndLoadStartDateTimeBetween(@Param("center") Center center,
                                                                   @Param("startDateTime") LocalDateTime startDateTime,
                                                                   @Param("endDateTime") LocalDateTime endDateTime);

    // 2. 센터 코드, 담당자Id를 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.center = :center AND d.users = :users " +
            "AND d.loadingStartTime " +
            "BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterAndUsersAndLoadStartDateTimeBetween(@Param("center") Center center,
                                                                             @Param("users") Users users,
                                                                             @Param("startDateTime") LocalDateTime startDateTime,
                                                                             @Param("endDateTime") LocalDateTime endDateTime);

    // 3. 센터, 배차 번호를 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.center = :center " +
            "AND d.dispatchNumber = :dispatchCode " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterAndDispatchCodeAndLoadStartDateTimeBetween(@Param("center") Center center,
                                                                                  @Param("dispatchCode") String dispatchCode,
                                                                                  @Param("startDateTime") LocalDateTime startDateTime,
                                                                                  @Param("endDateTime") LocalDateTime endDateTime);

    // 4. 센터, 담당자, 배차 번호를 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.center = :center AND d.users = :users " +
            "AND d.dispatchNumber = :dispatchCode " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterAndUsersAndDispatchCodeAndLoadStartDateTimeBetween(@Param("center")Center center,
                                                                                            @Param("users") Users users,
                                                                                            @Param("dispatchCode") String dispatchCode,
                                                                                            @Param("startDateTime") LocalDateTime startDateTime,
                                                                                            @Param("endDateTime") LocalDateTime endDateTime);

    // 5. 센터, 배차명을 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.center = :center " +
            "AND d.dispatchName = :dispatchName " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterAndDispatchNameAndLoadStartDateTimeBetween(@Param("center") Center center,
                                                                                  @Param("dispatchName") String dispatchName,
                                                                                  @Param("startDateTime") LocalDateTime startDateTime,
                                                                                  @Param("endDateTime") LocalDateTime endDateTime);

    // 6. 센터, 담당자, 배차명을 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.center = :center AND d.users = :users " +
            "AND d.dispatchName = :dispatchName " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterAndUsersAndDispatchNameAndLoadStartDateTimeBetween(@Param("center") Center center,
                                                                                            @Param("users") Users users,
                                                                                            @Param("dispatchName") String dispatchName,
                                                                                            @Param("startDateTime") LocalDateTime startDateTime,
                                                                                            @Param("endDateTime") LocalDateTime endDateTime);

    // 7. 센터, 기사명 기준으로 조회
    @Query("SELECT DISTINCT dn FROM DispatchNumber dn " +
            "JOIN FETCH dn.dispatchList d " +
            "JOIN FETCH d .sm sm " +
            "WHERE dn.center = :center " +
            "AND dn.loadingStartTime BETWEEN :startDateTime AND :endDateTime " +
            "AND sm.smName = :smName")
    List<DispatchNumber> findByCenterAndSmNameAndLoadStartDateTimeBetween(
            @Param("center") Center center,
            @Param("smName") String smName,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // 8. 센터, 담당자, 기사명 기준으로 조회
    @Query("SELECT DISTINCT dn FROM DispatchNumber dn " +
            "JOIN FETCH dn.dispatchList d " +
            "JOIN FETCH d .sm sm " +
            "WHERE dn.center = :center " +
            "AND dn.users = :users " +
            "AND dn.loadingStartTime BETWEEN :startDateTime AND :endDateTime " +
            "AND sm.smName = :smName")
    List<DispatchNumber> findByCenterAndUsersAndSmNameAndLoadStartDateTimeBetween(
            @Param("center") Center center,
            @Param("users") Users users,
            @Param("smName") String smName,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // 9. id 리스트로 DispatchNumber 리스트 조회
    List<DispatchNumber> findByIdIn(List<Long> ids);

    // 10. 	id 리스트로 연관 테이블 전체 삭제
    void deleteByIdIn(List<Long> ids);

    @Query("select dn from DispatchNumber dn join fetch dn.dispatchList d where dn.id = :id")
    Optional<DispatchNumber> findByIdWithJoin(Long id);

    default DispatchNumber findByIdWithJoinOrThrow(Long id) {
        return findByIdWithJoin(id).orElseThrow(() -> new DispatchNumberException(DispatchNumberErrorCode.NOT_FOUND_DISPATCH_NUMBER));
    }

    default int countByCenterAndLoadingStartTimeOnDate(Center center, LocalDateTime loadingStartTime) {
        LocalDateTime startOfDay = loadingStartTime.toLocalDate().atStartOfDay(); // 해당 날짜의 시작 시간
        LocalDateTime endOfDay = loadingStartTime.toLocalDate().atTime(LocalTime.MAX); // 해당 날짜의 끝 시간

        return countByCenterAndLoadingStartTimeBetween(center, startOfDay, endOfDay);
    }

    int countByCenterAndLoadingStartTimeBetween(Center center, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
