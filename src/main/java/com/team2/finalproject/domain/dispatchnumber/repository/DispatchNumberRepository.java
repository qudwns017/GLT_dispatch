package com.team2.finalproject.domain.dispatchnumber.repository;

import com.team2.finalproject.domain.dispatchnumber.model.entity.DispatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DispatchNumberRepository extends JpaRepository<DispatchNumber, Long> {

    // 1. 센터 코드를 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.centerId = :centerId " +
            "AND d.loadingStartTime " +
            "BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterIdAndLoadStartDateTimeBetween(@Param("centerId") Long centerId,
                                                                   @Param("startDateTime") LocalDateTime startDateTime,
                                                                   @Param("endDateTime") LocalDateTime endDateTime);

    // 2. 센터 코드, 담당자Id를 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.centerId = :centerId AND d.adminId = :adminId " +
            "AND d.loadingStartTime " +
            "BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterIdAndAdminIdAndLoadStartDateTimeBetween(@Param("centerId") Long centerId,
                                                                             @Param("adminId") Long adminId,
                                                                             @Param("startDateTime") LocalDateTime startDateTime,
                                                                             @Param("endDateTime") LocalDateTime endDateTime);

    // 3. 센터 코드, 배차 번호를 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.centerId = :centerId " +
            "AND d.dispatchNumber = :dispatchCode " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterIdAndDispatchCodeAndLoadStartDateTimeBetween(@Param("centerId") Long centerId,
                                                                                  @Param("dispatchCode") String dispatchCode,
                                                                                  @Param("startDateTime") LocalDateTime startDateTime,
                                                                                  @Param("endDateTime") LocalDateTime endDateTime);

    // 4. 센터 코드, 담당자Id, 배차 번호를 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.centerId = :centerId AND d.adminId = :adminId " +
            "AND d.dispatchNumber = :dispatchCode " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterIdAndAdminIdAndDispatchCodeAndLoadStartDateTimeBetween(@Param("centerId") Long centerId,
                                                                                            @Param("adminId") Long adminId,
                                                                                            @Param("dispatchCode") String dispatchCode,
                                                                                            @Param("startDateTime") LocalDateTime startDateTime,
                                                                                            @Param("endDateTime") LocalDateTime endDateTime);

    // 5. 센터 코드, 배차명을 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.centerId = :centerId " +
            "AND d.dispatchName = :dispatchName " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterIdAndDispatchNameAndLoadStartDateTimeBetween(@Param("centerId") Long centerId,
                                                                                  @Param("dispatchName") String dispatchName,
                                                                                  @Param("startDateTime") LocalDateTime startDateTime,
                                                                                  @Param("endDateTime") LocalDateTime endDateTime);

    // 6. 센터 코드, 담당자Id, 배차명을 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.centerId = :centerId AND d.adminId = :adminId " +
            "AND d.dispatchName = :dispatchName " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByCenterIdAndAdminIdAndDispatchNameAndLoadStartDateTimeBetween(@Param("centerId") Long centerId,
                                                                                            @Param("adminId") Long adminId,
                                                                                            @Param("dispatchName") String dispatchName,
                                                                                            @Param("startDateTime") LocalDateTime startDateTime,
                                                                                            @Param("endDateTime") LocalDateTime endDateTime);

    // 9. 여러 개의 id, 센터 코드 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.id IN :ids AND d.centerId = :centerId " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByIdInAndCenterIdAndLoadStartDateTimeBetween(@Param("ids") List<Long> ids,
                                                                          @Param("centerId") Long centerId,
                                                                          @Param("startDateTime") LocalDateTime startDateTime,
                                                                          @Param("endDateTime") LocalDateTime endDateTime);

    // 10. 여러 개의 id, 센터 코드, 담당자Id 기준으로 조회
    @Query("SELECT d FROM DispatchNumber d " +
            "WHERE d.id IN :ids AND d.centerId = :centerId AND d.adminId = :adminId " +
            "AND d.loadingStartTime BETWEEN :startDateTime AND :endDateTime")
    List<DispatchNumber> findByIdInAndCenterIdAndAdminIdAndLoadStartDateTimeBetween(@Param("ids") List<Long> ids,
                                                                                    @Param("centerId") Long centerId,
                                                                                    @Param("adminId") Long adminId,
                                                                                    @Param("startDateTime") LocalDateTime startDateTime,
                                                                                    @Param("endDateTime") LocalDateTime endDateTime);
}
