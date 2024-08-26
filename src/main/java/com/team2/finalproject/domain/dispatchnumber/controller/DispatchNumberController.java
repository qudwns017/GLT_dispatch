package com.team2.finalproject.domain.dispatchnumber.controller;

import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse;
import com.team2.finalproject.domain.dispatchnumber.service.DispatchNumberService;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatchNumber")
@RequiredArgsConstructor
public class DispatchNumberController implements SwaggerDispatchNumberController{

    private final DispatchNumberService dispatchNumberService;

    @GetMapping("/{dispatchCodeId}/vehicle-control")
    public ResponseEntity<DispatchListResponse> getDispatchList(@PathVariable Long dispatchCodeId) {
        DispatchListResponse response = dispatchNumberService.getDispatchList(dispatchCodeId);
        return ApiResponse.OK(response);
    }
}
