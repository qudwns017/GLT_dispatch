package com.team2.finalproject.global.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue()
                .set(getRefreshTokenKey(username),
                        refreshToken,
                        Duration.ofSeconds(jwtProvider.getRefreshExpireTime()));
    }

    public String getRefreshToken(String username) {
        return (String) redisTemplate.opsForValue().get(getRefreshTokenKey(username));
    }

    public void addToBlacklist(String accessToken) {
        redisTemplate.opsForValue()
                .set(getBlacklistKey(accessToken),
                        "true",
                        Duration.ofHours(12)); // 12시간(리프레시 토큰 유효 시간) 동안 블랙리스트 처리
    }

    public boolean isBlacklistedToken(String accessToken) {
        return redisTemplate.opsForValue().get(getBlacklistKey(accessToken)) != null;
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete(getRefreshTokenKey(username));
    }

    private String getRefreshTokenKey(String username) {
        return "refresh:" + username;
    }

    private String getBlacklistKey(String accessToken) {
        return "blacklist:" + accessToken;
    }
}