package com.team2.finalproject.global.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Util {

    public static LocalDateTime addDelayTime(LocalDateTime baseTime, LocalTime delayTime) {
        return baseTime.plusHours(delayTime.getHour())
                .plusMinutes(delayTime.getMinute())
                .plusSeconds(delayTime.getSecond());
    }

    public static int convertLocalTimeToMinutes(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    public static String[] splitAddress(String fullAddress) {
        // 공백의 인덱스를 추적
        int lastSpaceIndex = fullAddress.lastIndexOf(' ');
        int secondLastSpaceIndex = fullAddress.lastIndexOf(' ', lastSpaceIndex - 1);

        if (secondLastSpaceIndex != -1) {
            String address = fullAddress.substring(0, secondLastSpaceIndex).trim();
            String detailAddress = fullAddress.substring(secondLastSpaceIndex + 1).trim();
            return new String[]{address, detailAddress};
        }

        // 공백이 1번 이하인 경우 전체를 주소로 반환
        return new String[]{fullAddress.trim(), ""};
    }
}

