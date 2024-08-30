package com.team2.finalproject.domain.dispatch.controller;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchConfirmRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchUpdateRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.IssueRequest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Dispatch", description = "배차")
public interface SwaggerDispatchController {

    @Operation(summary = "배차 확정", description = "배차를 확정하여 데이터베이스에 저장합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배차 확정 성공")
    })
    ResponseEntity<Void> confirmDispatch(@RequestBody DispatchConfirmRequest request, @AuthenticationPrincipal
    UserDetailsImpl user);

    @Operation(summary = "차량관제 탭, 배차 삭제", description = "차량관제 탭에서 배차 삭제를 합니다.")
    ResponseEntity<Void> cancelDispatch(
            @Parameter(description = "배차 취소 요청 정보") @RequestBody DispatchCancelRequest cancelDispatchRequest
    );

    @Operation(summary = "배차 변경", description = "배차 탭에서 배차 변경을 합니다. (DispatchUpdateRequest, DispatchUpdateResponse)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배차 변경 성공", content = @Content(schema = @Schema(implementation = DispatchUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DispatchUpdateResponse> updateDispatch(
            @Parameter(description = "배차 변경 요청 정보") @RequestBody DispatchUpdateRequest request);

    @Operation(summary = "배송 이슈", description = "배송 이슈를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이슈 내용 변경 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "배차가 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<Void> updateIssue(@PathVariable long dispatchId,
                                     @RequestBody IssueRequest request);
}