package com.team2.finalproject.domain.transportorder.controller;

import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchResponse;
import com.team2.finalproject.domain.transportorder.model.dto.request.TransportOrderRequest;
import com.team2.finalproject.domain.transportorder.model.dto.request.ValidationListRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndSmIdResponse;
import com.team2.finalproject.domain.transportorder.model.dto.response.TransportOrderResponse;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Transport Order", description = "운송 주문")
public interface SwaggerTransportOrderController {
    @Operation(summary = "주문 확인 및 배차", description = "입력된 주문 정보를 경로 최적화 후 최초 배차를 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최초 배차 성공",
                    content = @Content(schema = @Schema(implementation = DispatchResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<DispatchResponse> TransportOrderToDispatch(
            @RequestBody TransportOrderRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails);

    @Operation(summary = "운송 주문 엑셀 양식 다운로드", description = "수동 배차에서 사용되는 운송 주문 엑셀 양식을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "엑셀 다운로드 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
    })
    ResponseEntity<Void> downloadOrderFormExcel(HttpServletResponse response);

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "운송 주문 데이터 검증 성공",
                    content = @Content(schema = @Schema(implementation = SmNameAndSmIdResponse.class))),
            @ApiResponse(responseCode = "401", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(summary = "운송 주문 데이터 검증", description = "등록된 운송 주문의 SM명과 SM 아이디의 존재를 확인합니다.")
    ResponseEntity<List<SmNameAndSmIdResponse>> validateSmNameAndSmIds(
            @RequestBody ValidationListRequest request);

    @Operation(summary = "주문 상세 조회", description = "배차 상세에서 주문 상세를 조회 (TransportOrderResponse)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공", content = @Content(schema = @Schema(implementation = TransportOrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "배송처를 찾지 못하였습니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "운송실행주문을 찾지 못하였습니다", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<TransportOrderResponse> getTransportOrderById(
        @PathVariable Long transportOrderId
    );
}
