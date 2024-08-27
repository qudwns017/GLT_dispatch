package com.team2.finalproject.domain.dispatch.controller;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchSearchRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchUpdateRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchSearchResponse;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchUpdateResponse;
import com.team2.finalproject.domain.dispatch.service.DispatchService;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dispatch")
public class DispatchController implements SwaggerDispatchController{

    private final DispatchService dispatchService;

    @GetMapping
    public ResponseEntity<DispatchSearchResponse> searchDispatches(@ModelAttribute DispatchSearchRequest request,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

        DispatchSearchResponse response = dispatchService.searchDispatches(request, userDetails.getId());
        return ApiResponse.OK(response);
    }

    @PutMapping
    public ResponseEntity<DispatchUpdateResponse> updateDispatch(@RequestBody DispatchUpdateRequest request){
        DispatchUpdateResponse response = dispatchService.updateDispatch(request);
        return ApiResponse.OK(response);
    }
}
