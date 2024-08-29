package com.team2.finalproject.domain.transportorder.model.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record TransportOrderRequest(
        LocalDateTime loadingStartTime, // 상차 시작 시간
        String dispatchName, // 배차 이름
        List<OrderRequest> orderReuquestList // 주문 목록
) {}
