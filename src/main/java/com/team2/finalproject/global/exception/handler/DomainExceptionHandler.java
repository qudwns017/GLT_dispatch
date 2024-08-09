package com.team2.finalproject.global.exception.handler;

import com.team2.finalproject.global.exception.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
@Order(value = Integer.MIN_VALUE)
@Slf4j
public class DomainExceptionHandler {

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorResponse> exception(HttpStatusCodeException exception) {
        log.warn("", exception);
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(new ErrorResponse(exception.getStatusText(), exception.getMessage()));
    }
}
