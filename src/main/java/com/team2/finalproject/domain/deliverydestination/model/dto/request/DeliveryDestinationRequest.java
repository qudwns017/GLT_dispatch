package com.team2.finalproject.domain.deliverydestination.model.dto.request;

import com.team2.finalproject.domain.center.model.entity.Center;
import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DeliveryDestinationRequest(
        @Schema(example = "1", description = "센터 ID")
        long centerId,
        @Schema(example = "충남정보센터", description = "배송처 이름")
        String destinationName,
        @Schema(example = "충남 논산시 중앙대로 374번길 41-11", description = "주소")
        String address,
        @Schema(example = "1층 물류센터", description = "상세주소")
        String detailAddress,
        @Schema(example = "32934", description = "우편번호")
        String zipCode,
        @Schema(example = "김물류", description = "담당자명")
        String adminName,
        @Schema(example = "01012345678", description = "연락처")
        String phoneNumber,
        @Schema(example = "36.3214", description = "위도")
        double latitude,
        @Schema(example = "127.1724", description = "경도")
        double longitude,
        @Schema(example = "example", description = "진입불가 톤 코드")
        String restrictedTonCode,
        @Schema(example = "윙바디 진입 불가", description = "비고")
        String comment,
        @Schema(example = "70", description = "작업추가 소요시간")
        int delayTime) {

    public static DeliveryDestination toEntity(DeliveryDestinationRequest request, Center center) {
        return DeliveryDestination.builder().
                center(center).
                destinationName(request.destinationName()).
                roadAddress(request.address()).
                detailAddress(request.detailAddress()).
                zipCode(request.zipCode()).
                managerName(request.adminName()).
                phoneNumber(request.phoneNumber()).
                latitude(request.latitude()).
                longitude(request.longitude()).
                restrictedTonCode(request.restrictedTonCode()).
                comment(request.comment()).
                delayTime(request.delayTime()).
                build();
    }
}