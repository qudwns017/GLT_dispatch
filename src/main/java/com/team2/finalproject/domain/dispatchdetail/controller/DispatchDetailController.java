package com.team2.finalproject.domain.dispatchdetail.controller;

import com.team2.finalproject.domain.dispatchdetail.model.dto.response.DispatchDetailResponse;
import com.team2.finalproject.domain.dispatchdetail.service.DispatchDetailService;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dispatch-detail")
public class DispatchDetailController implements SwaggerDispatchDetailController{
    final DispatchDetailService dispatchDetailService;

    @GetMapping("/{dispatchId}/vehicle-control")
    public ResponseEntity<DispatchDetailResponse> getDispatchDetail(@PathVariable Long dispatchId) {
        DispatchDetailResponse response = dispatchDetailService.getDispatchDetail(dispatchId);
        return ApiResponse.OK(response);
    }
}
