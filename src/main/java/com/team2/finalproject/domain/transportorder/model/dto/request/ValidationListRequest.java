package com.team2.finalproject.domain.transportorder.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ValidationListRequest(
        @Schema(example = "[{\"centerId\": 1, \"zipCode\": \"12345\"},{\"centerId\": 2, \"zipCode\": \"67890\"}]",
                description = "센터 ID와 우편번호 리스트")
        List<SmNameAndZipCodeRequest> requests
) {
}
