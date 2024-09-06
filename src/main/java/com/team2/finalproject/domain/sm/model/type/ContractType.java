package com.team2.finalproject.domain.sm.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContractType {
    JIIP("지입"),
    DELIVERY("택배")
    ;

    private final String contractType;
}
