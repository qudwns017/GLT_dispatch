package com.team2.finalproject.domain.dispatchnumber.model.entity;

import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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

    @Column(nullable = false, length = 20)
    private String dispatchCode; // 배차번호

    @Column(nullable = false, length = 50)
    private String dispatchName; // 배차명

    @Column(nullable = false)
    private LocalDateTime loadStartDateTime; // 상차 시작 일시

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus status = DispatchStatus.WAITING; // 상태
}
