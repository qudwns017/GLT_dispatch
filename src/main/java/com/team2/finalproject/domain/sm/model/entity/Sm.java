package com.team2.finalproject.domain.sm.model.entity;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.vehicle.model.entity.Vehicle;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Sm extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String smName;  // SM명

    @Column(nullable = false, length = 20)
    private String logisticsCode;  // 물류코드

    @Column(nullable = false)
    private LocalDate joinDate;  // 입사일

    @Column(nullable = false, length = 100)
    private String address;  // 주소

    @Column(nullable = false)
    private LocalTime workStartTime;  // 근무시작시간

    @Column(nullable = false)
    private LocalTime breakStartTime;  // 휴게시작시간

    @Column(nullable = false)
    private LocalTime breakTime;  // 휴게시간

    @Setter
    @Column(nullable = false)
    private Boolean isDriving;  // 상태  주행여부

    @ManyToOne(fetch = FetchType.LAZY)
    private Center center;  // 센터 ID

    @OneToOne(mappedBy = "sm")
    private Users users;

    @OneToOne(mappedBy = "sm")
    private Vehicle vehicle;

    @OneToMany(mappedBy = "sm")
    private List<Dispatch> dispatchList;
}