package com.team2.finalproject.domain.dispatch.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record DispatchUpdateRequest(
    @NotNull LocalDateTime loadingStartTime,
    @Valid List<Order> orderList
) {

    public record Order(
        @NotBlank String address,
        @NotNull Double lat,
        @NotNull Double lon,
        @NotNull int expectedServiceDuration
    ){

    }

}
