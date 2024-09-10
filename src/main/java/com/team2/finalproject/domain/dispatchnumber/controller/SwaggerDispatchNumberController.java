package com.team2.finalproject.domain.dispatchnumber.controller;

import com.team2.finalproject.domain.dispatchnumber.model.dto.request.DispatchNumberSearchRequest;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchNumberSearchResponse;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse;
import com.team2.finalproject.global.exception.response.ErrorResponse;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "DispatchNumber", description = "배차번호")
public interface SwaggerDispatchNumberController {
    @Operation(summary = "차량관제 탭, 배차코드 상세 조회", description = "차량관제 탭에서 선택한 배차코드에 대한 정보를 조회합니다. (DispatchListResponse)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "배차코드 상세 조회 성공", content = @Content(schema = @Schema(implementation = DispatchListResponse.class))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "배차코드를 찾지 못했습니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "센터를 찾지 못했습니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "배송처를 찾지 못했습니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DispatchListResponse> getDispatchList(
        @PathVariable Long dispatchCodeId
    );

    @Operation(summary = "차량관제 탭, 배차 검색", description = "차량관제 탭에서 배차 검색을 합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "배차 검색 성공", content = @Content(schema = @Schema(implementation = DispatchNumberSearchResponse.class))),
        @ApiResponse(responseCode = "400", description = "올바르지 않은 검색 옵션 입니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DispatchNumberSearchResponse> searchDispatches(
            @Parameter(description = "배차 검색 요청 정보") @ModelAttribute DispatchNumberSearchRequest dispatchNumberSearchRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}