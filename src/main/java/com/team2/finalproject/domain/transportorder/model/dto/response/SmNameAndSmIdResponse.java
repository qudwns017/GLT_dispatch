package com.team2.finalproject.domain.transportorder.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

public record SmNameAndSmIdResponse(
        @Schema(description = "기사 검증 리스트")
        List<ValidList> validList
) {
    @Builder
    public record ValidList(
            @Schema(example = "true", description = "기사명 검증")
            boolean smNameValid,
            @Schema(example = "1", description = "기사 ID")
            int smId
    ) {
    }
}