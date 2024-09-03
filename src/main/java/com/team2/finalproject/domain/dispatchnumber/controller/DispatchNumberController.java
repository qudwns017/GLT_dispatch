package com.team2.finalproject.domain.dispatchnumber.controller;

import com.team2.finalproject.domain.dispatchnumber.model.dto.request.DispatchNumberSearchRequest;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchNumberSearchResponse;
import com.team2.finalproject.domain.dispatchnumber.model.dto.response.DispatchListResponse;
import com.team2.finalproject.domain.dispatchnumber.service.DispatchNumberService;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch-number")
@RequiredArgsConstructor
public class DispatchNumberController implements SwaggerDispatchNumberController{

    private final DispatchNumberService dispatchNumberService;

    @GetMapping("/{dispatchCodeId}/vehicle-control")
    public ResponseEntity<DispatchListResponse> getDispatchList(@PathVariable Long dispatchCodeId) {
        DispatchListResponse response = dispatchNumberService.getDispatchList(dispatchCodeId);
        return ApiResponse.OK(response);
    }

    @GetMapping
    public ResponseEntity<DispatchNumberSearchResponse> searchDispatches(@ModelAttribute DispatchNumberSearchRequest request,
                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

        DispatchNumberSearchResponse response = dispatchNumberService.searchDispatches(request, userDetails);
        return ApiResponse.OK(response);
    }
}
