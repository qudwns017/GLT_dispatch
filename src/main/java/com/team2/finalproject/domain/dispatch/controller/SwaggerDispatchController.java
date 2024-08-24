package com.team2.finalproject.domain.dispatch.controller;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchSearchRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchSearchResponse;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Dispatch", description = "배차")
public interface SwaggerDispatchController {
    @Operation(summary = "차량관제 탭, 배차 검색", description = "차량관제 탭에서 배차 검색을 합니다.")
    ResponseEntity<DispatchSearchResponse> searchDispatches(
            @Parameter(description = "배차 검색 요청 정보") @RequestBody DispatchSearchRequest dispatchSearchRequest,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}