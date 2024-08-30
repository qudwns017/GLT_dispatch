package com.team2.finalproject.domain.users.controller;

import com.team2.finalproject.domain.users.model.dto.request.LoginRequest;
import com.team2.finalproject.domain.users.model.dto.response.LoginResponse;
import com.team2.finalproject.domain.users.model.dto.request.RegisterAdminRequest;
import com.team2.finalproject.domain.users.model.dto.request.RegisterDriverRequest;
import com.team2.finalproject.global.security.service.SecurityService;
import com.team2.finalproject.global.util.cookies.CookieUtil;
import com.team2.finalproject.global.util.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController implements SwaggerUsersController{

    private final SecurityService securityService;

    // TODO: 규격이 정해지면 @Valid 하기
    @PostMapping("/register/admin")
    public ResponseEntity<Void> registerAdmin(@RequestBody RegisterAdminRequest registerAdminRequest) {
        securityService.registerAdmin(registerAdminRequest);
        return ApiResponse.OK();
    }

    // TODO: 규격이 정해지면 @Valid 하기
    @PostMapping("/register/driver")
    public ResponseEntity<Void> registerDriver(@RequestBody RegisterDriverRequest registerDriverRequest) {
        securityService.registerDriver(registerDriverRequest);
        return ApiResponse.OK();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = securityService.login(loginRequest, response);
        return ApiResponse.OK(loginResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        securityService.logout(request);
        CookieUtil.deleteCookie(response, "accessToken");
        CookieUtil.deleteCookie(response, "refreshToken");
        return ApiResponse.OK();
    }

    @GetMapping("/withdraw")
    public ResponseEntity<Void> withdraw(HttpServletRequest request, HttpServletResponse response) {
        securityService.withdraw(request);
        CookieUtil.deleteCookie(response, "accessToken");
        CookieUtil.deleteCookie(response, "refreshToken");
        return ApiResponse.DELETED();
    }
}
