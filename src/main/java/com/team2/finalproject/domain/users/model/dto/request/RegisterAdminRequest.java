package com.team2.finalproject.domain.users.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminRequest {

    private long centerId;
    private String name;
    private String username;
    private String password;
    private String phoneNumber;
}
