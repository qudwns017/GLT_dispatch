package com.team2.finalproject.domain.deliverydestination.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record UpdateDeliveryDestinationRequest(
        @Schema(example = "윙바디 8T", description = "진입불가 톤 코드")
        String restrictedTonCode,
        @Schema(example = "윙바디 진입 불가", description = "비고")
        String comment,
        @Schema(example = "70", description = "작업추가 소요시간")
        Integer delayTime
) {
    @Builder
    public UpdateDeliveryDestinationRequest(String restrictedTonCode, String comment, Integer delayTime) {
        this.restrictedTonCode = restrictedTonCode;
        this.comment = comment;
        this.delayTime = delayTime;
    }
}
