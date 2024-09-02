package com.team2.finalproject.domain.dispatch.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record DispatchUpdateRequest(
    @Schema(example = "1", description = "기사id")
    @NotNull Long smId,
    @Schema(example = "2024-06-15T09:00:00", description = "상차 시작 시간", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull LocalDateTime loadingStartTime,
    @Valid List<Order> orderList
) {

    public record Order(
        @Schema(example = "서울시 강동구 천호동", description = "주소(주소가 아니더라도 특정할 수 있는 데이터 ex)start ,stopover1)", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String address,
        @Schema(example = "38.3333", description = "위도", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double lat,
        @Schema(example = "127.243", description = "경도", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull Double lon,
        @Schema(example = "60", description = "예상 작업시간", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull int expectedServiceDuration
    ){

    }

}
