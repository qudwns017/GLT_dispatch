package com.team2.finalproject.domain.transportorder.controller;

import com.team2.finalproject.domain.transportorder.model.dto.request.ValidationListRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndPostalCodeResponse;
import com.team2.finalproject.domain.transportorder.service.TransportOrderService;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransportOrderController {

    private final TransportOrderService validationService;

    @GetMapping("/api/transport-orders/valid")
    public ResponseEntity<List<SmNameAndPostalCodeResponse>> validateSmNameAndPostalCodes(
            @RequestBody ValidationListRequest request) {

        List<SmNameAndPostalCodeResponse> results = validationService.validateSmNameAndPostalCodes(request.requests());

        return ApiResponse.OK(results);
    }
}
