package com.team2.finalproject.domain.users.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminRequest {

    @Schema(example = "1", description = "센터 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private long centerId;

    @Schema(example = "John Doe", description = "관리자 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(example = "admin123", description = "관리자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(example = "password", description = "관리자 PW", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(example = "010-1234-5678", description = "관리자 전화번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;
}