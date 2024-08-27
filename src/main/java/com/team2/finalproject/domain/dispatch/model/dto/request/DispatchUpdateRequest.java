package com.team2.finalproject.domain.dispatch.model.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record DispatchUpdateRequest(
    LocalDateTime loadingStartTime,
    List<Order> orderList
) {

    public record Order(
        String address,
        Double lat,
        Double lon,
        int expectedServiceDuration
    ){

    }

}
