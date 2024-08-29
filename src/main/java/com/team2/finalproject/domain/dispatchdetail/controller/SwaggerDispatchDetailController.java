package com.team2.finalproject.domain.dispatchdetail.controller;

import com.team2.finalproject.domain.dispatchdetail.model.dto.response.DispatchDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}