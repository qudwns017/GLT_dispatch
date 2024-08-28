package com.team2.finalproject.domain.dispatch.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResponse {
    private String dispatchCode;  // 배차코드 = 배차번호
    private String dispatchName;  // 배차명
    private int totalOrder;  // 총 주문
    private int totalErrorNum; // 오류주문 수
    private int totalTime;  // 총 예상시간
    private int totalFloorAreaRatio; // 총 용적률

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime loadingStartTime; // 상차 시작 시간

    private StartStopoverResponse startStopoverResponse;  // 시작경유지
    private List<CourseResponse> course;  // 경로별 리스트
}
