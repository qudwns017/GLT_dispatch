package com.team2.finalproject.domain.users.model.dto.result;

import com.team2.finalproject.domain.users.model.dto.response.LoginResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResult {
    private LoginResponse loginResponse;
    private String accessToken;
    private String refreshToken;
}
