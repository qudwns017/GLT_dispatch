package com.team2.finalproject.domain.users.controller;

import com.team2.finalproject.domain.users.model.dto.request.LoginRequest;
import com.team2.finalproject.domain.users.model.dto.request.RegisterAdminRequest;
import com.team2.finalproject.domain.users.model.dto.request.RegisterDriverRequest;
import com.team2.finalproject.domain.users.model.dto.request.RegisterSuperAdminRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "사용자")
public interface SwaggerUsersController {
    @Operation(summary = "최고 관리자 등록", description = "최고 관리자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "최고 관리자 등록 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자")
    })
    ResponseEntity<Void> registerSuperAdmin(@RequestBody RegisterSuperAdminRequest registerSuperAdminRequest);

    @Operation(summary = "관리자 등록", description = "관리자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 등록 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자"),
            @ApiResponse(responseCode = "404", description = "존재하는 센터가 아님")
    })
    ResponseEntity<Void> registerAdmin(@RequestBody RegisterAdminRequest registerAdminRequest);

    @Operation(summary = "기사 등록", description = "기사를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기사 등록 성공"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자"),
            @ApiResponse(responseCode = "404", description = "존재하는 센터/Sm이 아님")
    })
    ResponseEntity<Void> registerDriver(@RequestBody RegisterDriverRequest registerDriverRequest);

    @Operation(summary = "사용자 로그인", description = "로그인을 시도합니다.")
    @PostMapping("/api/users/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "존재하는 사용자가 아님")
    })
    default ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 로그아웃", description = "로그아웃을 시도합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<Void> withdraw(HttpServletRequest request, HttpServletResponse response);
}
