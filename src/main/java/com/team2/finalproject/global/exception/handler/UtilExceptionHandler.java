package com.team2.finalproject.global.exception.handler;

import com.team2.finalproject.global.exception.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
@Order(value = 1)
@Slf4j
public class UtilExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> exception(WebClientResponseException exception) {
        log.warn("", exception);
        return ResponseEntity
            .status(exception.getStatusCode())
            .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), exception.getMessage()));
    }
}
