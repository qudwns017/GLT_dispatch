package com.team2.finalproject.domain.users.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(example = "admin", description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,

        @Schema(example = "password", description = "사용자 PW", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}
