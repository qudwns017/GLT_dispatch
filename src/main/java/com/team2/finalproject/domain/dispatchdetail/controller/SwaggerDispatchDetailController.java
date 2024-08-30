package com.team2.finalproject.domain.dispatchdetail.controller;

import com.team2.finalproject.domain.dispatchdetail.model.dto.response.DispatchDetailResponse;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "DispatchDetail", description = "차량 관제 배차 상세")
public interface SwaggerDispatchDetailController {

    @Operation(summary = "차량 관제 배차 상세 조회", description = "특정 배차 ID에 대한 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "배차 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 배차입니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 센터입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 배송처입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    @GetMapping("/{dispatchId}/vehicle-control")
    ResponseEntity<DispatchDetailResponse> getDispatchDetail(
            @Parameter(description = "조회할 배차 ID", required = true)
            @PathVariable Long dispatchId
            );

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