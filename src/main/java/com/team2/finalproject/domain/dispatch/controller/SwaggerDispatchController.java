package com.team2.finalproject.domain.dispatch.controller;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchSearchRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchUpdateRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchSearchResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchUpdateResponse;
import com.team2.finalproject.global.exception.response.ErrorResponse;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Dispatch", description = "배차")
public interface SwaggerDispatchController {
    @Operation(summary = "차량관제 탭, 배차 검색", description = "차량관제 탭에서 배차 검색을 합니다.")
    ResponseEntity<DispatchSearchResponse> searchDispatches(
            @Parameter(description = "배차 검색 요청 정보") @ModelAttribute DispatchSearchRequest dispatchSearchRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "차량관제 탭, 배차 삭제", description = "차량관제 탭에서 배차 삭제를 합니다.")
    ResponseEntity<Void> cancelDispatch(
            @Parameter(description = "배차 취소 요청 정보") @RequestBody DispatchCancelRequest cancelDispatchRequest
    );

    @Operation(summary = "배차 변경", description = "배차 탭에서 배차 변경을 합니다. (DispatchUpdateRequest, DispatchUpdateResponse)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "배차 변경 성공", content = @Content(schema = @Schema(implementation = DispatchUpdateResponse.class))),
        @ApiResponse(responseCode = "401", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DispatchUpdateResponse> updateDispatch(
        @Parameter(description = "배차 변경 요청 정보") @RequestBody @Valid DispatchUpdateRequest request);
}