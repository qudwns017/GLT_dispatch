package com.team2.finalproject.global.util.request;

import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Builder
public record OptimizationRequest(
        LocalDateTime startTime,
        LocalTime restStartTime,
        LocalTime restDuration,
        Stopover startStopover,
        List<Stopover> stopoverList
) {}
