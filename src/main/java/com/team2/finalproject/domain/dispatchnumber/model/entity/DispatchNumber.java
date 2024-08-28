package com.team2.finalproject.domain.dispatchnumber.model.entity;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.dispatch.model.entity.Dispatch;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DispatchNumber extends BaseEntity {

    @Column(nullable = false)
    private LocalDateTime loadingStartTime;

    @Column(nullable = false, length = 20)
    private String dispatchNumber; // 배차번호

    @Column(nullable = false, length = 50)
    private String dispatchName; // 배차명

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchNumberStatus status = DispatchNumberStatus.WAITING; // 상태

    @ManyToOne(fetch = FetchType.LAZY)
    private Users users; // 담당자

    @ManyToOne(fetch = FetchType.LAZY)
    private Center center; // 소속 센터

    @OneToMany(mappedBy = "dispatchNumber", cascade = CascadeType.REMOVE)
    private List<Dispatch> dispatchList;

}
