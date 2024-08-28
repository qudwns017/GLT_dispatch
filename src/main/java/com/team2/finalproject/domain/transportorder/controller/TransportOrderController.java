package com.team2.finalproject.domain.transportorder.controller;

import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchResponse;
import com.team2.finalproject.domain.transportorder.model.dto.request.TransportOrderRequest;
import com.team2.finalproject.domain.transportorder.model.dto.request.ValidationListRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndZipCodeResponse;
import com.team2.finalproject.domain.transportorder.service.TransportOrderService;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.util.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/transport-order")
@RequiredArgsConstructor
public class TransportOrderController implements SwaggerTransportOrderController{

    private final TransportOrderService transportOrderService;

    @PostMapping
    public ResponseEntity<DispatchResponse> TransportOrderToDispatch(
            @RequestBody TransportOrderRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        DispatchResponse dispatchResponse = transportOrderService.processTransportOrder(request, userDetails.getId());
        return ApiResponse.OK(dispatchResponse);
    }

    @GetMapping("/excel-example")
    public ResponseEntity<Void> downloadOrderFormExcel(HttpServletResponse response) {
        String fileName = "운송_주문_양식.xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFileName);

        transportOrderService.downloadOrderFormExcel(response);

        return ApiResponse.OK();
    }

    @PostMapping("/valid")
    public ResponseEntity<List<SmNameAndZipCodeResponse>> validateSmNameAndZipCodes(
            @RequestBody ValidationListRequest request) {

        List<SmNameAndZipCodeResponse> results = transportOrderService.validateSmNameAndZipCodes(request.requests());

        return ApiResponse.OK(results);
    }    
}
