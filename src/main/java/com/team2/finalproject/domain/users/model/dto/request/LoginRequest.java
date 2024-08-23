package com.team2.finalproject.domain.users.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(example = "asdasd123", description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(example = "password", description = "사용자 PW", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
