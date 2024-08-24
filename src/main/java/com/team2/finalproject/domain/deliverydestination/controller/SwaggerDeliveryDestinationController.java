package com.team2.finalproject.domain.deliverydestination.controller;

import com.team2.finalproject.domain.deliverydestination.model.dto.request.DeliveryDestinationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Delivery Destination", description = "배송처")
public interface SwaggerDeliveryDestinationController {
    @Operation(summary = "센터/배송처 상세 정보 조회", description = "센터 또는 배송처의 상제 정보를 조회합니다.")
    ResponseEntity<?> getCenterOrDeliveryDestinationInfo(
            @PathVariable(value = "place-id") long placeId,
            @RequestParam(value = "is-center") boolean isCenter);

    @Operation(summary = "배송지 추가", description = "배송지를 추가합니다.\n(화면 설계서 상 사용되지 않을 기능이지만 개발 상의 편의를 위하여 추가합니다.)\"")
    ResponseEntity<?> addDeliveryDestination(@RequestBody DeliveryDestinationRequest request);
}
