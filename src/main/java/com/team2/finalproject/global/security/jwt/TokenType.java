package com.team2.finalproject.global.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenType {

    ACCESS("ACCESS"),
    REFRESH("REFRESH");

    private final String type;
}

