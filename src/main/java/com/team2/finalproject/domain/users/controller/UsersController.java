package com.team2.finalproject.domain.users.controller;

import com.team2.finalproject.domain.users.model.dto.LoginRequest;
import com.team2.finalproject.domain.users.model.dto.LoginResponse;
import com.team2.finalproject.domain.users.model.dto.RegisterAdminRequest;
import com.team2.finalproject.domain.users.model.dto.RegisterDriverRequest;
import com.team2.finalproject.domain.users.service.UsersService;
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
public class UsersController {

    private final UsersService usersService;

    // TODO: 규격이 정해지면 @Valid 하기
    @PostMapping("/register/admin")
    public ResponseEntity<Void> registerAdmin(@RequestBody RegisterAdminRequest registerAdminRequest) {
        usersService.registerAdmin(registerAdminRequest);
        return ApiResponse.OK();
    }

    // TODO: 규격이 정해지면 @Valid 하기
    @PostMapping("/register/driver")
    public ResponseEntity<Void> registerDriver(@RequestBody RegisterDriverRequest registerDriverRequest) {
        usersService.registerDriver(registerDriverRequest);
        return ApiResponse.OK();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = usersService.login(loginRequest, response);
        return ApiResponse.OK(loginResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        usersService.logout(request);
        CookieUtil.deleteCookie(response, "accessToken");
        CookieUtil.deleteCookie(response, "refreshToken");
        return ApiResponse.OK();
    }

    @GetMapping("/withdraw")
    public ResponseEntity<Void> withdraw(HttpServletRequest request, HttpServletResponse response) {
        usersService.withdraw(request);
        CookieUtil.deleteCookie(response, "accessToken");
        CookieUtil.deleteCookie(response, "refreshToken");
        return ApiResponse.DELETED();
    }
}
