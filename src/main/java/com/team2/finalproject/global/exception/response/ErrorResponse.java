package com.team2.finalproject.global.exception.response;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Builder;

@Builder
public record ErrorResponse (
        String statusMessage,
        String message
){
    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .statusMessage(errorCode.getHttpStatus().getReasonPhrase())
                .message(errorCode.getMessage())
                .build();
    }
}