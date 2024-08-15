package com.team2.finalproject.global.security.filter;

import com.team2.finalproject.global.security.details.UserDetailsServiceImpl;
import com.team2.finalproject.global.security.exception.SecurityErrorCode;
import com.team2.finalproject.global.security.exception.SecurityException;
import com.team2.finalproject.global.security.jwt.JwtProvider;
import com.team2.finalproject.global.security.jwt.TokenService;
import com.team2.finalproject.global.security.jwt.TokenStatus;
import com.team2.finalproject.global.security.jwt.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws IOException, ServletException {

        String accessToken = jwtProvider.resolveToken(request, TokenType.ACCESS);
        String cookieRefreshToken = jwtProvider.resolveToken(request, TokenType.REFRESH);
        if (accessToken != null) {
            if (tokenService.isBlacklistedToken(accessToken)) {
                throw new SecurityException(SecurityErrorCode.BLACKLISTED_TOKEN);
            }

            TokenStatus tokenStatus = jwtProvider.validateToken(accessToken, TokenType.ACCESS);
            String username = jwtProvider.getUserNameFromToken(accessToken, TokenType.ACCESS);
            String redisRefreshToken = tokenService.getRefreshToken(username);
            if (tokenStatus == TokenStatus.VALID) {
                // 엑세스 토큰의 유효기간이 5분 미만이면, 재발급
                if (isAccessTokenExpiringSoon(accessToken) && cookieRefreshToken.equals(redisRefreshToken)) {
                    log.info("JWT 토큰 재발급 필요");
                    refreshToken(response, username);
                    tokenService.addToBlacklist(accessToken);
                } else {
                    setAuthentication(username);
                }
            } else if(tokenStatus == TokenStatus.EXPIRED && cookieRefreshToken.equals(redisRefreshToken)) {
                log.info("JWT 토큰 재발급 필요(만료)");
                refreshToken(response, username);
                tokenService.addToBlacklist(accessToken);
            } else if (tokenStatus == TokenStatus.INVALID) {
                throw new SecurityException(SecurityErrorCode.INVALID_TOKEN);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isAccessTokenExpiringSoon(String accessToken) {
        Claims claims = jwtProvider.getUserInfoFromToken(accessToken, TokenType.ACCESS);
        Date expiration = claims.getExpiration();
        Date now = new Date();
        long diff = (expiration.getTime() - now.getTime()) / 1000;
        return diff < 300;
    }

    private void refreshToken(HttpServletResponse response, String username) {
        String newAccessToken = jwtProvider.generateToken(username, TokenType.ACCESS);
        jwtProvider.addTokenCookie(response, newAccessToken, TokenType.ACCESS);
        log.info("Access 토큰 재발급: username={}, NewAccessToken={}", username, newAccessToken);
        setAuthentication(username);
    }

    private void setAuthentication(String username) {
        log.info("Authentication 설정 시도");
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Authentication 설정: {}", username);
    }
}