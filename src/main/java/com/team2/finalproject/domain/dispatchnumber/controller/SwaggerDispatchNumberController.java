package com.team2.finalproject.domain.dispatchnumber.controller;

import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse;
import com.team2.finalproject.global.exception.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "DispatchNumber", description = "배차번호")
public interface SwaggerDispatchNumberController {
    @Operation(summary = "차량관제 탭, 배차코드 상세 조회", description = "차량관제 탭에서 선택한 배차코드에 대한 정보를 조회합니다. (DispatchListResponse)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "배차코드 상세 조회 성공", content = @Content(schema = @Schema(implementation = DispatchListResponse.class))),
        @ApiResponse(responseCode = "401", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "배차코드를 찾지 못했습니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "센터를 찾지 못했습니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "배송처를 찾지 못했습니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DispatchListResponse> getDispatchList(
        @PathVariable Long dispatchCodeId
    );
}