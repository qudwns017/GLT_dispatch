package com.team2.finalproject.domain.dispatch.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record DispatchUpdateRequest(
        @Schema(example = "1", description = "기사id")
        @NotNull Long smId,
        @Schema(example = "[1,2,3,4]", description = "배차 번호내 기사id리스트")
        @NotNull List<Long> smIdList,
        @Schema(example = "0.1", description = "모든 배차의 볼륨 합", requiredMode = Schema.RequiredMode.REQUIRED)
        Double totalVolume,
        @Schema(example = "10.0", description = "모든 배차의 무게 합", requiredMode = Schema.RequiredMode.REQUIRED)
        Double totalWeight,
        @Schema(example = "2024-06-15T09:00:00", description = "상차 시작 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull LocalDateTime loadingStartTime,
        @Valid List<Order> orderList
) {
    public record Order(
            @Schema(example = "충남 천안시 서북구 백석로 123", description = "주소", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank String roadAddress,
            @Schema(example = "지하 1층", description = "상세 주소", requiredMode = Schema.RequiredMode.REQUIRED)
            String detailAddress,
            @Schema(example = "0.1", description = "볼륨", requiredMode = Schema.RequiredMode.REQUIRED)
            Double volume,
            @Schema(example = "10.0", description = "무게", requiredMode = Schema.RequiredMode.REQUIRED)
            Double weight,
            @Schema(example = "36.4501", description = "위도", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull Double lat,
            @Schema(example = "127.1234", description = "경도", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull Double lon,
            @Schema(example = "60", description = "예상 작업시간", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull int expectedServiceDuration,
            @Schema(example = "2024-06-15", description = "희망 도착일", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull LocalDate serviceRequestDate,
            @Schema(example = "11:00:00", description = "희망 도착시간", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalTime serviceRequestTime
    ) {

    }

}
