package com.team2.finalproject.domain.users.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDriverRequest {

    private long centerId;
    private long smId;
    private String name;
    private String username;
    private String encryptedPassword;
    private String phoneNumber;
}
