package com.team2.finalproject.domain.sm.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;


public class SmException extends HttpStatusCodeException {

    public SmException(ErrorCode errorCode) {

        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
