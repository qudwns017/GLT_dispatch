package com.team2.finalproject.domain.dispatch.controller;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Dispatch", description = "배차")
public interface SwaggerDispatchController {

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