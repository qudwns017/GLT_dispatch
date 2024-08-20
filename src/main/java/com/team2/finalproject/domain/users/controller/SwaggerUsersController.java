package com.team2.finalproject.domain.users.controller;

import com.team2.finalproject.domain.users.model.dto.LoginRequest;
import com.team2.finalproject.domain.users.model.dto.LoginResponse;
import com.team2.finalproject.domain.users.model.dto.RegisterAdminRequest;
import com.team2.finalproject.domain.users.model.dto.RegisterDriverRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "사용자")
public interface SwaggerUsersController {
    @Operation(summary = "관리자 등록", description = "관리자를 등록합니다.")
    ResponseEntity<Void> registerAdmin(@RequestBody RegisterAdminRequest registerAdminRequest);

    @Operation(summary = "SM 등록", description = "기사를 등록합니다.")
    ResponseEntity<Void> registerDriver(@RequestBody RegisterDriverRequest registerDriverRequest);

    @Operation(summary = "사용자 로그인", description = "로그인을 시도합니다.")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response);

    @Operation(summary = "사용자 로그아웃", description = "로그아웃을 시도합니다.")
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    ResponseEntity<Void> withdraw(HttpServletRequest request, HttpServletResponse response);
}
