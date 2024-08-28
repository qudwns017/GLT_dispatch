package com.team2.finalproject.global.util.response;

import com.team2.finalproject.global.util.request.Stopover;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptimizationResponse {
    private double totalDistance;
    private int totalTime;
    private LocalDateTime startTime;
    private Stopover startStopover;
    private List<ResultStopover> resultStopoverList;
    private List<Coordinate> coordinates;
}
