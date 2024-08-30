package com.team2.finalproject.domain.transportorder.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SmNameAndSmIdResponse(
        @Schema(example = "true", description = "기사명 검증")
        boolean smNameValid,
        @Schema(example = "1", description = "기사 ID")
        int smId
) {
}