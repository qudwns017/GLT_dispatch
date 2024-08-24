package com.team2.finalproject.domain.deliverydestination.controller;

import com.team2.finalproject.domain.center.service.CenterService;
import com.team2.finalproject.domain.deliverydestination.model.dto.request.DeliveryDestinationRequest;
import com.team2.finalproject.domain.deliverydestination.model.dto.request.UpdateDeliveryDestinationRequest;
import com.team2.finalproject.domain.deliverydestination.model.dto.response.DeliveryDestinationResponse;
import com.team2.finalproject.domain.deliverydestination.service.DeliveryDestinationService;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery-destination")
public class DeliveryDestinationController implements SwaggerDeliveryDestinationController {

    private final DeliveryDestinationService deliveryDestinationService;
    private final CenterService centerService;

    @GetMapping("/{placeId}")
    public ResponseEntity<?> getCenterOrDeliveryDestinationInfo(
            @PathVariable long placeId,
            @RequestParam(value = "is-center") boolean isCenter) {

        if (!isCenter) {
            return ApiResponse.OK(deliveryDestinationService.getDeliveryDestination(placeId));
        } else {
            return ApiResponse.OK(centerService.getCenter(placeId));
        }
    }

    @PostMapping
    public ResponseEntity<?> addDeliveryDestination(@RequestBody DeliveryDestinationRequest request) {
        DeliveryDestinationResponse response = deliveryDestinationService.addDeliveryDestination(request);
        return ApiResponse.CREATED(response);
    }

    @PatchMapping("/{deliveryDestinationId}")
    public ResponseEntity<?> updateDeliveryDestination(
            @PathVariable long deliveryDestinationId,
            @RequestBody UpdateDeliveryDestinationRequest request
    ) {
        deliveryDestinationService.updateDeliveryDestination(deliveryDestinationId, request);
        return ApiResponse.OK();
    }
}
