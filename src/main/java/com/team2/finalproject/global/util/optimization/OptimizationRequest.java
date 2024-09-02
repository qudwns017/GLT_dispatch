package com.team2.finalproject.global.util.optimization;

import com.team2.finalproject.global.util.request.Stopover;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptimizationRequest {

    private LocalDateTime startTime;
    private Stopover startStopover;
    private List<Stopover> stopoverList;
    private LocalTime restStartTime;
    private LocalTime restDuration;

    private OptimizationRequest(LocalDateTime startTime, Stopover startStopover,List<Stopover> stopoverList, LocalTime restStartTime, LocalTime restDuration) {
        this.startTime = startTime;
        this.startStopover = startStopover;
        this.stopoverList = stopoverList;
        this.restStartTime = restStartTime;
        this.restDuration = restDuration;
    }

    public static OptimizationRequest of(LocalDateTime startTime, Stopover startStopover,List<Stopover> stopoverList, LocalTime restStartTime, LocalTime restDuration){
        return new OptimizationRequest(startTime,startStopover,stopoverList,restStartTime,restDuration);
    }

}