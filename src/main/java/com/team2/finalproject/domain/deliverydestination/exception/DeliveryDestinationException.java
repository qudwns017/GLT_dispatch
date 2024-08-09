package com.team2.finalproject.domain.deliverydestination.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import org.springframework.web.client.HttpStatusCodeException;

public class DeliveryDestinationException extends HttpStatusCodeException {

    public DeliveryDestinationException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
