package com.team2.finalproject.domain.center.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;


public class CenterException extends HttpStatusCodeException {

    public CenterException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
