package com.team2.finalproject.global.util.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressInfo {
    private String customerAddress;
    private double lat;
    private double lon;
}
