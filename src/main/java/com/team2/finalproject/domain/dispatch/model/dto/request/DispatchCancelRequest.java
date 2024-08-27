package com.team2.finalproject.domain.dispatch.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DispatchCancelRequest(
        @Schema(example = "true", description = "배차 상태가 주행 중인지 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isInTransit,

        @Schema(example = "[\"1\", \"2\"]", description = "배차 번호 id 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        List<Long> dispatchNumberIds
) {}