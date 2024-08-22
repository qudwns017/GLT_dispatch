package com.team2.finalproject.domain.users.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDriverRequest {

    @Schema(example = "1", description = "센터 ID")
    private long centerId;

    @Schema(example = "1", description = "SM ID")
    private long smId;

    @Schema(example = "John Smith", description = "기사 이름")
    private String name;

    @Schema(example = "driver123", description = "기사 ID")
    private String username;

    @Schema(example = "password", description = "기사 PW")
    private String password;

    @Schema(example = "010-9876-5432", description = "기사 전화번호")
    private String phoneNumber;
}