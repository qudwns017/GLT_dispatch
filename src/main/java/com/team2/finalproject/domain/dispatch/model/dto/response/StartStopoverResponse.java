package com.team2.finalproject.domain.dispatch.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartStopoverResponse {
    private long centerId;  // 센터 ID (출발지)
    private String fullAddress;  // 출발지 주소
    private double lat;  // 출발지 위도
    private double lon;  // 출발지 경도
    private LocalTime expectedServiceDuration;  // 상차 작업 시간 (기본 1시간)
}