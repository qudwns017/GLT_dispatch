package com.team2.finalproject.global.util.optimization;

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

    @Getter
    @NoArgsConstructor
    public static class Stopover{

        private String address;
        private Double lat;
        private Double lon;
        private LocalTime delayTime;

        private Stopover(String address, Double lat, Double lon, LocalTime delayTime) {
            this.address = address;
            this.lat = lat;
            this.lon = lon;
            this.delayTime = delayTime;
        }
        public static Stopover of(String address, Double lat, Double lon, LocalTime delayTime) {
            return new Stopover(address,lat,lon,delayTime);
        }

    }

}