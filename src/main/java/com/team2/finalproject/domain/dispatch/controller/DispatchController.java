package com.team2.finalproject.domain.dispatch.controller;

import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchCancelRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.DispatchSearchRequest;
import com.team2.finalproject.domain.dispatch.model.dto.request.IssueRequest;
import com.team2.finalproject.domain.dispatch.model.dto.response.DispatchSearchResponse;
import com.team2.finalproject.domain.dispatch.service.DispatchService;
import com.team2.finalproject.global.security.details.UserDetailsImpl;
import com.team2.finalproject.global.util.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dispatch")
public class DispatchController implements SwaggerDispatchController {
    private final DispatchService dispatchService;

    @GetMapping
    public ResponseEntity<DispatchSearchResponse> searchDispatches(@ModelAttribute DispatchSearchRequest request,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

        DispatchSearchResponse response = dispatchService.searchDispatches(request, userDetails.getId());
        return ApiResponse.OK(response);
    }

    @PatchMapping
    public ResponseEntity<Void> cancelDispatch(@RequestBody DispatchCancelRequest request) {
        dispatchService.cancelDispatch(request);
        return ApiResponse.OK();
    }

    @PatchMapping("/{dispatchId}/issue")
    public ResponseEntity<Void> updateIssue(@PathVariable long dispatchId,
                                            @RequestBody IssueRequest request) {
        dispatchService.updateIssue(dispatchId, request);
        return ApiResponse.OK();
    }
}
