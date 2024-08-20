package com.team2.finalproject.domain.transportorder.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SmNameAndPostalCodeResponse {

    private boolean postalCodeValid;

    private int deliveryDestinationId;

    private boolean smNameValid;

    private int smId;
}
