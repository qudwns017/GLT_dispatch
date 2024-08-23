package com.team2.finalproject.domain.center.controller;

import com.team2.finalproject.domain.center.service.CenterService;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.team2.finalproject.domain.center.model.dto.request.CenterRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/center")
public class CenterController implements SwaggerCenterController {

    private final CenterService centerService;

    @PostMapping
    public ResponseEntity<?> addCenter(@RequestBody CenterRequest request) {
        var response = centerService.addCenter(request);
        return ApiResponse.CREATED(response);
    }
}
