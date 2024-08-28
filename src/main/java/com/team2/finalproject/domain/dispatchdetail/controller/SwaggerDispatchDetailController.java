package com.team2.finalproject.domain.dispatchdetail.controller;

import com.team2.finalproject.global.exception.response.ErrorResponse;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "DispatchDetail", description = "배차상세")
public interface SwaggerDispatchDetailController {
    @Operation(summary = "배송 취소", description = "dispatchDetailId List를 입력받아 배송 취소")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "배송 취소 성공"),
        @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "올바르지 않은 배차상세 id가 있습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "해당 센터의 주문이 아닌 데이터가 존재합니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "배차상세에 맞는 운송실행주문이 없습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> cancelDispatchDetail(
        @RequestBody List<Long> dispatchDetailIdList,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}