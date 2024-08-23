package com.team2.finalproject.domain.users.service;

import com.team2.finalproject.domain.center.repository.CenterRepository;
import com.team2.finalproject.domain.sm.repository.SmRepository;
import com.team2.finalproject.domain.users.exception.UsersErrorCode;
import com.team2.finalproject.domain.users.exception.UsersException;
import com.team2.finalproject.domain.users.model.dto.request.LoginRequest;
import com.team2.finalproject.domain.users.model.dto.response.LoginResponse;
import com.team2.finalproject.domain.users.model.dto.request.RegisterAdminRequest;
import com.team2.finalproject.domain.users.model.dto.request.RegisterDriverRequest;
import com.team2.finalproject.domain.users.model.entity.Users;
import com.team2.finalproject.domain.users.model.type.Role;
import com.team2.finalproject.domain.users.repository.UsersRepository;
import com.team2.finalproject.global.security.exception.SecurityErrorCode;
import com.team2.finalproject.global.security.exception.SecurityException;
import com.team2.finalproject.global.security.jwt.JwtProvider;
import com.team2.finalproject.global.security.jwt.TokenService;
import com.team2.finalproject.global.security.jwt.TokenStatus;
import com.team2.finalproject.global.security.jwt.TokenType;
import com.team2.finalproject.global.util.cookies.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final CenterRepository centerRepository;
    private final SmRepository smRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    @Transactional
    public void registerAdmin(RegisterAdminRequest registerAdminRequest) {
        log.info("관리자 회원가입 시도: 센터 코드={}, 이름={}, 아이디={}", registerAdminRequest.centerId(), registerAdminRequest.name(), registerAdminRequest.username());

        if(usersRepository.existsByUsername(registerAdminRequest.username())) {
            throw new UsersException(UsersErrorCode.DUPLICATE_USERNAME);
        }

        // 이후 센터 데이터 추가 시 활성화
//        if(!centerRepository.existsById(registerAdminRequest.centerId())) {
//            throw new CenterException(CenterErrorCode.NOT_FOUND_CENTER);
//        }

        Users users = Users.builder()
                //.center(centerRepository.findById(registerAdminRequest.centerId()).get())
                .name(registerAdminRequest.name())
                .username(registerAdminRequest.username())
                .encryptedPassword(passwordEncoder.encode(registerAdminRequest.password()))
                .phoneNumber(registerAdminRequest.phoneNumber())
                .role(Role.ADMIN)
                .build();
        usersRepository.save(users);

        log.info("관리자 추가: {}", users.getName());
    }

    @Transactional
    public void registerDriver(RegisterDriverRequest registerDriverRequest) {
        log.info("기사 회원가입 시도: 센터 코드={}, 이름={}, 아이디={}", registerDriverRequest.centerId(), registerDriverRequest.name(), registerDriverRequest.username());

        if(usersRepository.existsByUsername(registerDriverRequest.username())) {
            throw new UsersException(UsersErrorCode.DUPLICATE_USERNAME);
        }

        // 이후 센터 데이터 추가 시 활성화
//        if(!centerRepository.existsById(registerDriverRequest.centerId())) {
//            throw new CenterException(CenterErrorCode.NOT_FOUND_CENTER);
//        }

//         이후 SM 데이터 추가 시 활성화
//        if (!smRepository.existsById(registerDriverRequest.smId())) {
//            throw new SmException(SmErrorCode.NOT_FOUND_SM);
//        }

        Users users = Users.builder()
                //.center(centerRepository.findById(registerDriverRequest.centerId()).get())
                //.sm(smRepository.findById(registerDriverRequest.smId()).get())
                .name(registerDriverRequest.name())
                .username(registerDriverRequest.username())
                .encryptedPassword(passwordEncoder.encode(registerDriverRequest.password()))
                .phoneNumber(registerDriverRequest.phoneNumber())
                .role(Role.DRIVER)
                .build();
        usersRepository.save(users);

        log.info("기사 추가: {}", users.getName());
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        log.info("사용자 로그인 시도: 아이디={}", loginRequest.username());

        Users users = usersRepository.findByUsernameOrThrow(loginRequest.username());

        if (!passwordEncoder.matches(loginRequest.password(), users.getEncryptedPassword())) {
            throw new UsersException(UsersErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = jwtProvider.generateToken(users.getUsername(), TokenType.ACCESS);
        jwtProvider.addTokenCookie(response, accessToken, TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(users.getUsername(), TokenType.REFRESH);
        jwtProvider.addTokenCookie(response, refreshToken, TokenType.REFRESH);
        tokenService.saveRefreshToken(users.getUsername(), refreshToken);
        log.info("사용자 로그인 성공: {}", users.getName());

        return LoginResponse.builder()
                .name(users.getName())
                .build();
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
