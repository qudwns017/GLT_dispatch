package com.team2.finalproject.domain.dispatch.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesResponse {
    private double lon;  // 경도
    private double lat;  // 위도
}
