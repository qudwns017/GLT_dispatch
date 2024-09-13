package com.team2.finalproject.domain.dispatch.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "배차코드 = 배차번호", example = "D123456789")
    private String dispatchCode;

    @Schema(description = "배차명", example = "배차1")
    private String dispatchName;

    @Schema(description = "총 주문", example = "100")
    private int totalOrder;

    @Schema(description = "오류주문 수", example = "5")
    private int totalErrorNum;

    @Schema(description = "총 예상시간 (분)", example = "480")
    private int totalTime;

    @Schema(description = "총 용적률", example = "85")
    private int totalFloorAreaRatio;

    @Schema(description = "상차 시작 시간", example = "2024-08-30T09:00:00")
    private LocalDateTime loadingStartTime;

    @Schema(description = "배송 유형", example = "지입")
    private String contractType;

    private StartStopoverResponse startStopoverResponse;

    private List<CourseResponse> course;
}
