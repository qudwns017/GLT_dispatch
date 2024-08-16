package com.team2.finalproject.domain.dispatchnumber.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;


public class DispatchNumberException extends HttpStatusCodeException {

    public DispatchNumberException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
