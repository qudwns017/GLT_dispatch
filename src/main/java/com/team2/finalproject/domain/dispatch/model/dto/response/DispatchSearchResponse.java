package com.team2.finalproject.domain.dispatch.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchSearchResponse {
    @Schema(example = "10", description = "진행중 ")
    private int inProgress;

    @Schema(example = "10", description = "대기 중")
    private int waiting;

    @Schema(example = "10", description = "완료")
    private int completed;

    @Schema(description = "검색 결과 목록")
    private List<DispatchResult> results;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DispatchResult {
        @Schema(example = "1", description = "배차id")
        private Long dispatchNumberId;

        @Schema(example = "50", description = "배차 진행률")
        private int progress;

        @Schema(example = "DC001", description = "배차 코드")
        private String dispatchCode;

        @Schema(example = "Dispatch 1", description = "배차명")
        private String dispatchName;

        @Schema(example = "2023-06-15T09:00:00", description = "배차 시작일시")
        private LocalDateTime startDateTime;

        @Schema(example = "100", description = "총 주문 수")
        private int totalOrder;

        @Schema(example = "5", description = "SM 수")
        private int smNum;

        @Schema(example = "John Doe", description = "담당자")
        private String manager;
    }
}