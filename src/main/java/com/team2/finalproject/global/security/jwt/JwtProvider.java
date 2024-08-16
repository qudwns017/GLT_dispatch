package com.team2.finalproject.global.security.jwt;

import com.team2.finalproject.global.security.exception.SecurityErrorCode;
import com.team2.finalproject.global.security.exception.SecurityException;
import com.team2.finalproject.global.util.cookies.CookieUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Getter
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
    @Value("${spring.jwt.access-secret}")
    private String accessSecret;
    @Value("${spring.jwt.refresh-secret}")
    private String refreshSecret;
    @Value("${spring.jwt.access-expiration}")
    private Long accessExpireTime;
    @Value("${spring.jwt.refresh-expiration}")
    private Long refreshExpireTime;
    private Key accessKey;
    private Key refreshKey;

    @PostConstruct
    public void init() {
        byte[] accessKeyBytes = Decoders.BASE64.decode(accessSecret);
        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshSecret);
        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }

    public String generateToken(String username, TokenType tokenType) {
        Date now = new Date();
        Date expiration =
                new Date(now.getTime() + (tokenType == TokenType.ACCESS ? accessExpireTime : refreshExpireTime));
        Key key = tokenType == TokenType.ACCESS ? accessKey : refreshKey;

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public TokenStatus validateToken(String token, TokenType tokenType) {
        try {
            Key key = tokenType == TokenType.ACCESS ? accessKey : refreshKey;
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.info("유효한 토큰");
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.info("만료된 토큰");
            return TokenStatus.EXPIRED;
        } catch (Exception e) {
            // 그 외의 예외 발생 시 (유효하지 않은 토큰)
            log.info("유효지 않은 토큰");
            return TokenStatus.INVALID;
        }
    }

    public Claims getUserInfoFromToken(String token, TokenType type) {
        Key key = type == TokenType.ACCESS ? accessKey : refreshKey;
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUserNameFromToken(String token, TokenType tokenType) {
        try {
            Key key = tokenType == TokenType.ACCESS ? accessKey : refreshKey;
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰인 경우 claims에서 사용자 이름을 가져옴
            return e.getClaims().getSubject();
        } catch (Exception e) {
            // 그 외의 예외 발생 시 (유효하지 않은 토큰)
            throw new SecurityException(SecurityErrorCode.INVALID_TOKEN);
        }
    }

    public String resolveToken(HttpServletRequest request, TokenType tokenType) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenType == TokenType.ACCESS && cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                } else if (tokenType == TokenType.REFRESH && cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void addTokenCookie(HttpServletResponse response, String accessToken, TokenType tokenType) {
        if (tokenType == TokenType.ACCESS) {
            CookieUtil.addCookie(response, "accessToken", accessToken, refreshExpireTime / 1000);
        } else {
            CookieUtil.addCookie(response, "refreshToken", accessToken, refreshExpireTime / 1000);
        }
    }
}