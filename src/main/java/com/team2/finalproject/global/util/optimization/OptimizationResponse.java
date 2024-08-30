package com.team2.finalproject.global.util.optimization;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public record OptimizationResponse (
    double totalDistance,
    long totalTime,
    LocalDateTime startTime,
    StartStopover startStopover,
    List<ResultStopover> resultStopoverList,
    List<Map<String,Double>> coordinates
){

    public record ResultStopover(
        String address,
        Double lat,
        Double lon,
        LocalTime delayTime,
        Double distance,
        Long timeFromPrevious,
        LocalDateTime startTime,
        LocalDateTime endTime
    ){

    }

    public record StartStopover(
        String address,
        Double lat,
        Double lon,
        LocalTime delayTime
    ){

    }

}