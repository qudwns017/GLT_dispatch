package com.team2.finalproject.domain.dispatch.model.dto.request;

import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DispatchSearchRequest {
    @Schema(example = "WAITING", description = "배차 상태")
    private DispatchNumberStatus status;

    @Schema(example = "true", description = "관리자 여부")
    private Boolean isManager;

    @Schema(example = "2024-06-01", description = "검색 시작일")
    private LocalDate startDate;

    @Schema(example = "2024-06-30", description = "검색 종료일")
    private LocalDate endDate;

    @Schema(example = "driver", description = "검색 옵션")
    private String searchOption;

    @Schema(example = "john", description = "검색 키워드")
    private String searchKeyword;
}