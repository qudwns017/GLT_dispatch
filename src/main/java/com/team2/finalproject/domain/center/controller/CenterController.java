package com.team2.finalproject.domain.center.controller;

import com.team2.finalproject.domain.center.model.dto.request.UpdateCenterRequest;
import com.team2.finalproject.domain.center.service.CenterService;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.team2.finalproject.domain.center.model.dto.response.CenterResponse;
import com.team2.finalproject.domain.center.model.dto.request.CenterRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/center")
public class CenterController implements SwaggerCenterController {

    private final CenterService centerService;

    @GetMapping("/{centerId}")
    public ResponseEntity<?> getCenter(@PathVariable long centerId) {
        CenterResponse response = centerService.getCenter(centerId);
        return ApiResponse.OK(response);
    }

    @PostMapping
    public ResponseEntity<?> addCenter(@RequestBody CenterRequest request) {
        CenterResponse response = centerService.addCenter(request);
        return ApiResponse.CREATED(response);
    }

    @PatchMapping("/{centerId}")
    public ResponseEntity<?> updateCenter(
            @PathVariable long centerId,
            @RequestBody UpdateCenterRequest request
    ) {
        centerService.updateCenter(centerId, request);
        return ApiResponse.OK();
    }
}
