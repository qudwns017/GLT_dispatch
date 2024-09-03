package com.team2.finalproject.global.security.service;

import com.team2.finalproject.domain.center.exception.CenterErrorCode;
import com.team2.finalproject.domain.center.exception.CenterException;
import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.sm.exception.SmErrorCode;
import com.team2.finalproject.domain.sm.exception.SmException;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.users.exception.UsersErrorCode;
import com.team2.finalproject.domain.users.exception.UsersException;
import com.team2.finalproject.domain.users.model.dto.request.RegisterSuperAdminRequest;
import com.team2.finalproject.domain.users.model.dto.request.RegisterAdminRequest;
import com.team2.finalproject.domain.users.model.dto.request.RegisterDriverRequest;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.repository.UsersRepository;
import com.team2.finalproject.global.security.exception.SecurityErrorCode;
import com.team2.finalproject.global.security.exception.SecurityException;
import com.team2.finalproject.global.security.jwt.JwtProvider;
import com.team2.finalproject.global.security.jwt.TokenService;
import com.team2.finalproject.global.security.jwt.TokenStatus;
import com.team2.finalproject.global.security.jwt.TokenType;
import com.team2.finalproject.global.util.cookies.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityService {

    private final UsersRepository usersRepository;
    private final CenterRepository centerRepository;
    private final SmRepository smRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    @Transactional
    public void registerSuperAdmin(RegisterSuperAdminRequest registerSuperAdminRequest) {
        log.info("최고 관리자 회원가입 시도: 이름={}, 아이디={}", registerSuperAdminRequest.name(), registerSuperAdminRequest.username());

        if(usersRepository.existsByUsername(registerSuperAdminRequest.username())) {
            throw new UsersException(UsersErrorCode.DUPLICATE_USERNAME);
        }

        Users users = registerSuperAdminRequest.toEntity();
        users.updatePassword(passwordEncoder.encode(registerSuperAdminRequest.password()));

        usersRepository.save(users);

        log.info("최고 관리자 추가: {}", users.getName());
    }

    @Transactional
    public void registerAdmin(RegisterAdminRequest registerAdminRequest) {
        log.info("관리자 회원가입 시도: 센터 코드={}, 이름={}, 아이디={}", registerAdminRequest.centerId(), registerAdminRequest.name(), registerAdminRequest.username());

        if(usersRepository.existsByUsername(registerAdminRequest.username())) {
            throw new UsersException(UsersErrorCode.DUPLICATE_USERNAME);
        }

        if(!centerRepository.existsById(registerAdminRequest.centerId())) {
            throw new CenterException(CenterErrorCode.NOT_FOUND_CENTER);
        }

        Users users = registerAdminRequest.toEntity();
        users.updatePassword(passwordEncoder.encode(registerAdminRequest.password()));
        users.updateCenter(centerRepository.findByIdOrThrow(registerAdminRequest.centerId()));

        usersRepository.save(users);

        log.info("관리자 추가: {}", users.getName());
    }

    @Transactional
    public void registerDriver(RegisterDriverRequest registerDriverRequest) {
        log.info("기사 회원가입 시도: 센터 코드={}, 이름={}, 아이디={}", registerDriverRequest.centerId(), registerDriverRequest.name(), registerDriverRequest.username());

        if(usersRepository.existsByUsername(registerDriverRequest.username())) {
            throw new UsersException(UsersErrorCode.DUPLICATE_USERNAME);
        }

        if(!centerRepository.existsById(registerDriverRequest.centerId())) {
            throw new CenterException(CenterErrorCode.NOT_FOUND_CENTER);
        }

        if (!smRepository.existsById(registerDriverRequest.smId())) {
            throw new SmException(SmErrorCode.NOT_FOUND_SM);
        }

        Users users = registerDriverRequest.toEntity();
        users.updatePassword(passwordEncoder.encode(registerDriverRequest.password()));
        users.updateCenter(centerRepository.findByIdOrThrow(registerDriverRequest.centerId()));
        users.updateSm(smRepository.findByIdOrThrow(registerDriverRequest.smId()));

        usersRepository.save(users);

        log.info("기사 추가: {}", users.getName());
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        log.info("사용자 로그아웃 시도");
        String username = invalidateToken(request);
        log.info("사용자 로그아웃 성공: {}", username);
    }

    @Transactional
    public void withdraw(HttpServletRequest request) {
        log.info("사용자 삭제 시도");
        String username = invalidateToken(request);
        usersRepository.deleteByUsername(username);
        log.info("사용자 삭제 성공: {}", username);
    }

    private String invalidateToken(HttpServletRequest request) {
        String accessToken = Objects.requireNonNull(CookieUtil.getCookie(request, "accessToken")).getValue();

        if(accessToken == null || jwtProvider.validateToken(accessToken, TokenType.ACCESS) != TokenStatus.VALID) {
            throw new SecurityException(SecurityErrorCode.INVALID_TOKEN);
        }

        String username = jwtProvider.getUserNameFromToken(accessToken, TokenType.ACCESS);
        tokenService.addToBlacklist(accessToken);
        tokenService.deleteRefreshToken(username);

        return username;
    }
}
