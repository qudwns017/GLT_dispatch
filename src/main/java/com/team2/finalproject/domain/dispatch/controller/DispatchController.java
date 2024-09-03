package com.team2.finalproject.domain.dispatch.controller;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchConfirmRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchUpdateRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.IssueRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchUpdateResponse;
import com.team2.finalproject.domain.dispatch.service.DispatchService;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.util.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dispatch")
public class DispatchController implements SwaggerDispatchController{
    private final DispatchService dispatchService;

    @PostMapping
    public ResponseEntity<Void> confirmDispatch(@RequestBody DispatchConfirmRequest request, @AuthenticationPrincipal
                                                UserDetailsImpl userDetails) {
        dispatchService.confirmDispatch(request, userDetails);
        return ApiResponse.CREATED();
    }

    @PatchMapping
    public ResponseEntity<Void> cancelDispatch(@RequestBody DispatchCancelRequest request) {
        dispatchService.cancelDispatch(request);
        return ApiResponse.OK();
    }

    @PutMapping
    public ResponseEntity<DispatchUpdateResponse> updateDispatch(@RequestBody @Valid DispatchUpdateRequest request){
        DispatchUpdateResponse response = dispatchService.updateDispatch(request);
        return ApiResponse.OK(response);
    }

    @PatchMapping("/{dispatchId}/issue")
    public ResponseEntity<Void> updateIssue(@PathVariable long dispatchId,
                                            @RequestBody IssueRequest request) {
        dispatchService.updateIssue(dispatchId, request);
        return ApiResponse.OK();
    }
}
