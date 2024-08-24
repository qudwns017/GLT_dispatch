package com.team2.finalproject.domain.transportorder.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SmNameAndZipCodeResponse(
        @Schema(example = "true", description = "우편번호 검증")
        boolean zipCodeValid,
        @Schema(example = "1", description = "배송처 ID")
        int deliveryDestinationId,
        @Schema(example = "true", description = "기사명 검증")
        boolean smNameValid,
        @Schema(example = "1", description = "기사 ID")
        int smId
) {
}