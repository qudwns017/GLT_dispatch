package com.team2.finalproject.domain.transportorder.controller;

import com.team2.finalproject.domain.transportorder.model.dto.request.ValidationListRequest;
import com.team2.finalproject.domain.transportorder.model.dto.response.SmNameAndZipCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Transport Order", description = "운송 주문")
public interface SwaggerTransportOrderController {
    @Operation(summary = "운송 주문 엑셀 양식 다운로드", description = "수동 배차에서 사용되는 운송 주문 엑셀 양식을 다운로드합니다.")
    ResponseEntity<Void> downloadOrderFormExcel(HttpServletResponse response);

    @Operation(summary = "운송 주문 데이터 검증", description = "등록된 운송 주문의 SM명과 배송처 우편번호의 존재를 확인합니다.")
    ResponseEntity<List<SmNameAndZipCodeResponse>> validateSmNameAndZipCodes(
            @RequestBody ValidationListRequest request);
}
