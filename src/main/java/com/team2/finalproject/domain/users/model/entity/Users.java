package com.team2.finalproject.domain.users.model.entity;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.sm.model.entity.Sm;
import com.team2.finalproject.domain.users.model.type.Role;
import com.team2.finalproject.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Users extends BaseEntity {

    @Column(nullable = false, length = 30)
    private String name;  // 이름

    @Column(nullable = false, length = 30)
    private String username;  // 아이디

    @Column(nullable = false, length = 80)
    private String encryptedPassword;  // 비밀번호

    @Column(nullable = false, length = 20)
    private String phoneNumber;  // 전화번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // 사용자 권한

    @ManyToOne(fetch = FetchType.LAZY)
    private Center center;

    @OneToOne(mappedBy = "users")
    private Sm sm;
}
