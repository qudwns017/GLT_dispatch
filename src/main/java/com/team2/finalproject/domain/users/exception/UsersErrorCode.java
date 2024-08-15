package com.team2.finalproject.domain.users.exception;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UsersErrorCode implements ErrorCode {
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하는 아이디가 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.CONFLICT, "비밀번호가 일치하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
