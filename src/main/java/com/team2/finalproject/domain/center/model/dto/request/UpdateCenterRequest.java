package com.team2.finalproject.domain.center.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateCenterRequest(@Schema(example = "1,2.5,5", description = "윙바디 진입 불가")
                                  String restrictedWingBody,  // 진입 불가 톤 코드
                                  @Schema(example = "1", description = "탑차 진입 불가")
                                  String restrictedBox,       // 진입 불가 톤 코드
                                  @Schema(example = "2.5,5", description = "카고 진입 불가")
                                  String restrictedCargo,     // 진입 불가 톤 코드
                                  @Schema(example = "윙바디 진입 불가", description = "비고")
                                  String comment,
                                  @Schema(example = "70", description = "작업추가 소요시간")
                                  Integer delayTime
) {
}
