package com.team2.finalproject.domain.dispatch.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DispatchStatus {
    IN_TRANSIT("주행중"),
    WAITING("주행대기"),
    COMPLETED("주행완료")
    ;

    private final String description;  // 상태에 대한 설명
}
