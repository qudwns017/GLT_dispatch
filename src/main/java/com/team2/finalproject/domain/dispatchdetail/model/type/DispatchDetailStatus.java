package com.team2.finalproject.domain.dispatchdetail.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DispatchDetailStatus {
    PENDING("운송대기"),
    STARTED("운송시작"),
    LOADED("상차완료"),
    UNLOADED("하차완료"),
    CANCELED("취소"),;

    private final String description;  // 상태에 대한 설명
}