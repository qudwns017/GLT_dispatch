package com.team2.finalproject.domain.transportorder.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SmNameRequest(
        @Schema(example = "홍길동", description = "기사 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        String smName
) {}