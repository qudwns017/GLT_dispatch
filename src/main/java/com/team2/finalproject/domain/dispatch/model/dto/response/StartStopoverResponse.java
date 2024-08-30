package com.team2.finalproject.domain.dispatch.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartStopoverResponse {
    @Schema(description = "센터 ID (출발지)", example = "123")
    private long centerId;

    @Schema(description = "출발지 주소", example = "서울시 강동구 천호동")
    private String fullAddress;

    @Schema(description = "출발지 위도", example = "37.5409")
    private double lat;

    @Schema(description = "출발지 경도", example = "127.1263")
    private double lon;

    @Schema(description = "상차 작업 시간 (기본 1시간)", example = "01:00")
    private LocalTime expectedServiceDuration;

    @Schema(description = "첫 경유지로 운송 시작 시간", example = "2024-08-30T10:00:00")
    private LocalDateTime departureTime;
}