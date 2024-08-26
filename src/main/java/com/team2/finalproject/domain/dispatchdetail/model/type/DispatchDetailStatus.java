package com.team2.finalproject.domain.dispatchdetail.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DispatchDetailStatus {
    MOVING("이동 중"),
    WORK_COMPLETED("작업완료"),
    WORK_WAITING("작업대기"),
    WORK_START("작업시작"),
    DELIVERY_DELAY("배송지연");

    private final String description;  // 상태에 대한 설명
}
