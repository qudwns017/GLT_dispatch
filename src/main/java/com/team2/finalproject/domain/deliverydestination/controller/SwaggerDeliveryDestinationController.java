package com.team2.finalproject.domain.deliverydestination.controller;

import com.team2.finalproject.domain.deliverydestination.model.dto.request.DeliveryDestinationRequest;
import com.team2.finalproject.domain.deliverydestination.model.dto.request.UpdateDeliveryDestinationRequest;
import com.team2.finalproject.domain.deliverydestination.model.dto.response.DeliveryDestinationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Delivery Destination", description = "배송처")
public interface SwaggerDeliveryDestinationController {
    @Operation(summary = "배송처 상세 정보 조회", description = "배송처의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송처 상세 정보 조회 성공", content = @Content(schema = @Schema(implementation = DeliveryDestinationResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 배송처", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> getDeliveryDestination(@PathVariable(value = "deliveryDestinationId") long deliveryDestinationId);

    @Operation(summary = "배송처 추가", description = "배송처를 추가합니다.\n(화면 설계서 상 사용되지 않을 기능이지만 개발 상의 편의를 위하여 추가합니다.)\"")
    ResponseEntity<?> addDeliveryDestination(@RequestBody DeliveryDestinationRequest request);

    @Operation(summary = "배송처 상세정보 변경", description = "배송처에 대한 상세정보를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송처 상세정보 변경 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 배송처", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> updateDeliveryDestination(@PathVariable long deliveryDestinationId,
                                                @RequestBody UpdateDeliveryDestinationRequest request);
}
