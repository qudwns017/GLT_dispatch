package com.team2.finalproject.domain.dispatchdetail.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;


public class DispatchDetailException extends HttpStatusCodeException {

    public DispatchDetailException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
