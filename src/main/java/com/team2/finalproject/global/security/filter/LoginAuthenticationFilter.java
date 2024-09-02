package com.team2.finalproject.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.finalproject.domain.users.model.dto.request.LoginRequest;
import com.team2.finalproject.domain.users.model.dto.response.LoginResponse;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.security.jwt.JwtProvider;
import com.team2.finalproject.global.security.jwt.TokenService;
import com.team2.finalproject.global.security.jwt.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    public LoginAuthenticationFilter(String loginUrl, AuthenticationManager authenticationManager,
                                     ObjectMapper objectMapper, JwtProvider jwtProvider, TokenService tokenService) {
        super(new AntPathRequestMatcher(loginUrl, "POST"));
        this.objectMapper = objectMapper;
        this.jwtProvider = jwtProvider;
        this.tokenService = tokenService;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

            return getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse authentication request body", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String username = userDetails.getUsername();

        String accessToken = jwtProvider.generateToken(username, TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(username, TokenType.REFRESH);

        tokenService.saveRefreshToken(username, refreshToken);

        jwtProvider.addTokenCookie(response, accessToken, TokenType.ACCESS);
        jwtProvider.addTokenCookie(response, refreshToken, TokenType.REFRESH);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        LoginResponse loginResponse = LoginResponse.builder()
                .name(userDetails.getUsers().getName())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), loginResponse);

        log.info("사용자 로그인 성공: {}", username);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.error("로그인 실패: {}", failed.getMessage());

        int statusCode;
        String errorMessage;

        if (failed instanceof UsernameNotFoundException) {
            statusCode = HttpStatus.NOT_FOUND.value();
            errorMessage = failed.getMessage();
        } else if (failed instanceof BadCredentialsException) {
            statusCode = HttpStatus.BAD_REQUEST.value();
            errorMessage = failed.getMessage();
        } else {
            statusCode = HttpStatus.UNAUTHORIZED.value();
            errorMessage = "로그인 실패";
        }

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");  // 문자 인코딩을 UTF-8로 설정

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "로그인 실패");
        errorResponse.put("message", errorMessage);

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
