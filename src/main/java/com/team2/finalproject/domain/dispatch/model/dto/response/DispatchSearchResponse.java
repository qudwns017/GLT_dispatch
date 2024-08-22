package com.team2.finalproject.domain.dispatch.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchSearchResponse {
    private int inProgress;
    private int waiting;
    private int completed;
    private List<DispatchResult> results;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DispatchResult {
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