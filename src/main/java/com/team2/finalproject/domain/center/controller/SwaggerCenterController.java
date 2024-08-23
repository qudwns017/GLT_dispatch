package com.team2.finalproject.domain.center.controller;

import com.team2.finalproject.domain.center.model.dto.request.CenterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Center", description = "센터")
public interface SwaggerCenterController {
    @Operation(summary = "센터 추가", description = "센터를 추가합니다.\n(화면 설계서 상 사용되지 않을 기능이지만 개발 상의 편의를 위하여 추가합니다.)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "센터 추가 성공", content = @Content(schema = @Schema(implementation = CenterRequest.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = CenterRequest.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 센터", content = @Content(schema = @Schema(implementation = CenterRequest.class)))
    })
    ResponseEntity<?> addCenter(@RequestBody CenterRequest request);
}