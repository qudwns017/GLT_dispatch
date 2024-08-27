package com.team2.finalproject.domain.dispatch.controller;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Dispatch", description = "배차")
public interface SwaggerDispatchController {

    @Operation(summary = "차량관제 탭, 배차 삭제", description = "차량관제 탭에서 배차 삭제를 합니다.")
    ResponseEntity<Void> cancelDispatch(
            @Parameter(description = "배차 취소 요청 정보") @RequestBody DispatchCancelRequest cancelDispatchRequest
    );
}