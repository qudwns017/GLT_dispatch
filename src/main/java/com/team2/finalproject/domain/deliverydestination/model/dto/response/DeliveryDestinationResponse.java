package com.team2.finalproject.domain.deliverydestination.model.dto.response;

import com.team2.finalproject.domain.deliverydestination.model.entity.DeliveryDestination;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record DeliveryDestinationResponse(
        @Schema(example = "1", description = "배송처 ID")
        long deliveryDestinationId,
        @Schema(example = "1", description = "센터 ID")
        long centerId,
        @Schema(example = "충남정보센터", description = "배송처 이름")
        String destinationName,
        @Schema(example = "충남 논산시 중앙대로 374번길 41-11", description = "도로명 주소")
        String roadAddress,
        @Schema(example = "충남 논산시 중앙동 41", description = "지번 주소")
        String lotNumberAddress,
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
        @Schema(example = "1,2.5,5", description = "윙바디 진입 불가")
        String restrictedWingBody,  // 진입 불가 톤 코드
        @Schema(example = "1", description = "탑차 진입 불가")
        String restrictedBox,       // 진입 불가 톤 코드
        @Schema(example = "2.5,5", description = "카고 진입 불가")
        String restrictedCargo,     // 진입 불가 톤 코드
        @Schema(example = "윙바디 진입 불가", description = "비고")
        String comment,
        @Schema(example = "70", description = "작업추가 소요시간")
        int delayTime,
        @Schema(example = "2024-08-26T14:51:51.980395", description = "최종 수정 일시")
        LocalDateTime updateAt
) {

    public static DeliveryDestinationResponse of(DeliveryDestination deliveryDestination) {
        return DeliveryDestinationResponse.builder().
                deliveryDestinationId(deliveryDestination.getId()).
                centerId(deliveryDestination.getCenter().getId()).
                destinationName(deliveryDestination.getDestinationName()).
                lotNumberAddress(deliveryDestination.getRoadAddress()).
                roadAddress(deliveryDestination.getRoadAddress()).
                detailAddress(deliveryDestination.getDetailAddress()).
                zipCode(deliveryDestination.getZipCode()).
                adminName(deliveryDestination.getManagerName()).
                phoneNumber(deliveryDestination.getPhoneNumber()).
                latitude(deliveryDestination.getLatitude()).
                longitude(deliveryDestination.getLongitude()).
                restrictedWingBody(deliveryDestination.getRestrictedWingBody()).
                restrictedBox(deliveryDestination.getRestrictedBox()).
                restrictedCargo(deliveryDestination.getRestrictedCargo()).
                comment(deliveryDestination.getComment()).
                delayTime(deliveryDestination.getDelayTime()).
                updateAt(deliveryDestination.getUpdateAt()).
                build();
    }
}