package com.team2.finalproject.domain.users.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;


public class UsersException extends HttpStatusCodeException {

    public UsersException(ErrorCode errorCode) {

        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
