package com.team2.finalproject.domain.dispatch.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record IssueRequest(
        @Schema(example = "배송이슈 및 기타사항을 입력합니다. 300자 이내로 입력해주세요. 배송이슈가 발생했습니다.",
                description = "배송이슈 및 기타사항", requiredMode = Schema.RequiredMode.REQUIRED)
        String issue
) {
}
