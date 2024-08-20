package com.team2.finalproject.domain.transportorder.controller;

import com.team2.finalproject.domain.transportorder.model.dto.request.ValidationListRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndPostalCodeResponse;
import com.team2.finalproject.domain.transportorder.service.TransportOrderService;
import com.team2.finalproject.global.util.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/transport-order")
@RequiredArgsConstructor
public class TransportOrderController {

    private final TransportOrderService transportOrderService;

    @GetMapping
    public ResponseEntity<Void> downloadOrderFormExcel(HttpServletResponse response) {
        String fileName = "운송_주문_양식.xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFileName);

        transportOrderService.downloadOrderFormExcel(response);

        return ApiResponse.OK();
    }

    @GetMapping("/valid")
    public ResponseEntity<List<SmNameAndPostalCodeResponse>> validateSmNameAndPostalCodes(
            @RequestBody ValidationListRequest request) {

        List<SmNameAndPostalCodeResponse> results = transportOrderService.validateSmNameAndPostalCodes(request.requests());

        return ApiResponse.OK(results);
    }    
}
