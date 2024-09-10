package com.team2.finalproject.global.util.request;

import lombok.Builder;

import java.time.LocalTime;

@Builder
public record Stopover(
        String address,
        double lat,
        double lon,
        LocalTime delayTime
) {

    public static Stopover of(String address, Double lat, Double lon, LocalTime delayTime) {
        return new Stopover(address,lat,lon,delayTime);
    }
}
