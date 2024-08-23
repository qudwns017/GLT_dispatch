package com.team2.finalproject.domain.users.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDriverRequest {

    @Schema(example = "1", description = "센터 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private long centerId;

    @Schema(example = "1", description = "SM ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private long smId;

    @Schema(example = "John Smith", description = "기사 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(example = "driver123", description = "기사 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(example = "password", description = "기사 PW", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(example = "010-9876-5432", description = "기사 전화번호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;
}