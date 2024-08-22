package com.team2.finalproject.domain.transportorder.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SmNameAndZipCodeResponse {

    private boolean zipCodeValid;

    private int deliveryDestinationId;

    private boolean smNameValid;

    private int smId;
}
