package com.team2.finalproject.global.security.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;

public class SecurityException extends HttpStatusCodeException {

    public SecurityException(ErrorCode errorCode) {

        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }

}
