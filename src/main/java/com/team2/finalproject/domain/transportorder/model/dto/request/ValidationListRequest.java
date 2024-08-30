package com.team2.finalproject.domain.transportorder.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ValidationListRequest(
        @Schema(
                description = "기사 이름과 ID 검증 요청 목록",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = """
            [
                {
                    "smName": "홍길동"
                },
                {
                    "smName": "이영희"
                }
            ]
            """
        )
        List<SmNameRequest> requests
) {
}
