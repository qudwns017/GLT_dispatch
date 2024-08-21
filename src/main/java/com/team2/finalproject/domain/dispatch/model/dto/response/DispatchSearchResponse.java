package com.team2.finalproject.domain.dispatch.model.dto.response;

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
        private int progress;
        private String dispatchCode;
        private String dispatchName;
        private LocalDateTime startDateTime;
        private int totalOrder;
        private int smNum;
        private String manager;
    }
}