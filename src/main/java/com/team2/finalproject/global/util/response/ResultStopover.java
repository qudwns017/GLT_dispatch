package com.team2.finalproject.global.util.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultStopover {
    private String address;
    private double lat;
    private double lon;
    private LocalTime delayTime;
    private double distance;
    private int timeFromPrevious;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
