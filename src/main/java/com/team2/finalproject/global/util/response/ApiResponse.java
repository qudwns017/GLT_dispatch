package com.team2.finalproject.global.util.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ApiResponse {

    public static <T> ResponseEntity<T> OK(T data) {
        return ResponseEntity.ok(data);
    }

    public static ResponseEntity<Void> OK() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public static <T> ResponseEntity<T> CREATED(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    public static ResponseEntity<Void> CREATED() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public static <T> ResponseEntity<T> DELETED() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
