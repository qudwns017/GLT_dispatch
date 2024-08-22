package com.team2.finalproject.domain.dispatchnumber.model.entity;

import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DispatchNumber extends BaseEntity {

    @Column(nullable = false)
    private Long centerId; // 센터Id

    @Column(nullable = false)
    private Long adminId; // 담당자Id

    @Column(nullable = false)
    private LocalDateTime loadingStartTime;

    @Column(nullable = false, length = 20)
    private String dispatchNumber; // 배차번호

    @Column(nullable = false, length = 50)
    private String dispatchName; // 배차명

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchNumberStatus status = DispatchNumberStatus.WAITING; // 상태

}
