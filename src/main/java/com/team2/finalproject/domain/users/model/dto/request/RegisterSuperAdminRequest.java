package com.team2.finalproject.domain.users.model.dto.request;

import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.model.type.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterSuperAdminRequest(

        @Schema(example = "John Doe", description = "관리자 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(example = "admin123", description = "관리자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,

        @Schema(example = "password", description = "관리자 PW", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,

        @Schema(example = "010-1234-5678", description = "관리자 전화번호", requiredMode = Schema.RequiredMode.REQUIRED)
        String phoneNumber
) {
        // 비밀 번호 제외
        public Users toEntity() {
                return Users.builder()
                        .name(this.name())
                        .username(this.username())
                        .phoneNumber(this.phoneNumber())
                        .role(Role.SUPER_ADMIN)
                        .build();
        }
}