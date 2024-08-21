package com.team2.finalproject.domain.transportorder.model.dto.request;

import java.util.List;

public record ValidationListRequest(
        List<SmNameAndPostalCodeRequest> requests
) {}
