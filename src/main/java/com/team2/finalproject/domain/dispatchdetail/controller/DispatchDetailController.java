package com.team2.finalproject.domain.dispatchdetail.controller;

import com.team2.finalproject.domain.dispatchdetail.service.DispatchDetailService;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.util.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatch-detail")
@RequiredArgsConstructor
public class DispatchDetailController implements SwaggerDispatchDetailController {

    private final DispatchDetailService dispatchDetailService;

    @PatchMapping("/vehicle-control")
    public ResponseEntity<Void> cancelDispatchDetail(
        @RequestBody List<Long> dispatchDetailIdList,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        dispatchDetailService.cancelDispatchDetailList(dispatchDetailIdList,userDetails.getUsers().getCenter().getId());
        return ApiResponse.OK();
    }

}
