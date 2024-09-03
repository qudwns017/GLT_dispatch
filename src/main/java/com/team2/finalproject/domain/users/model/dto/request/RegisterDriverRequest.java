package com.team2.finalproject.domain.users.model.dto.request;

import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.model.type.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterDriverRequest(
        @Schema(example = "1", description = "센터 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long centerId,

        @Schema(example = "1", description = "SM ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long smId,

        @Schema(example = "John Smith", description = "기사 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(example = "driver123", description = "기사 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,

        @Schema(example = "password", description = "기사 PW", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,

        @Schema(example = "010-9876-5432", description = "기사 전화번호", requiredMode = Schema.RequiredMode.REQUIRED)
        String phoneNumber
) {
        public Users toEntity() {
                return Users.builder()
                        .name(this.name())
                        .username(this.username())
                        .phoneNumber(this.phoneNumber())
                        .role(Role.DRIVER)
                        .build();
        }
}