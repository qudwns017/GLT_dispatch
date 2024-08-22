package com.team2.finalproject.domain.dispatch.model.dto.request;

import com.team2.finalproject.domain.dispatch.model.type.DispatchStatus;
import com.team2.finalproject.domain.dispatchnumber.model.type.DispatchNumberStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DispatchSearchRequest {
    private DispatchNumberStatus status;
    private Boolean isManager;
    private LocalDate startDate;
    private LocalDate endDate;
    private String searchOption;
    private String searchKeyword;
}
