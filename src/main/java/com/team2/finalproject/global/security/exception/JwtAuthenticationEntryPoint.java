package com.team2.finalproject.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.finalproject.global.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        SecurityException securityException = (SecurityException) request.getAttribute("securityException");
        // 기본 설정 - 로그인 하지 않은 상태에 대한 예외 처리
        if (securityException == null) {
            securityException = new SecurityException(SecurityErrorCode.UNAUTHORIZED_ACCESS);
        }

        response.setStatus(securityException.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String statusCode = securityException.getStatusCode().toString();
        ErrorResponse errorResponse = new ErrorResponse(statusCode.substring(statusCode.indexOf(" ") + 1),
                securityException.getStatusText());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}