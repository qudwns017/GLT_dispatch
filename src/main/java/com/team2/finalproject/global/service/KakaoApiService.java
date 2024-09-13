package com.team2.finalproject.global.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team2.finalproject.domain.transportorder.exception.TransportOrderErrorCode;
import com.team2.finalproject.domain.transportorder.exception.TransportOrderException;
import com.team2.finalproject.global.util.response.AddressInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoApiService {

    private final WebClient webClient;

    public KakaoApiService(@Value("${kakao.api.key}") String kakaoApiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey)
                .build();
    }

    public AddressInfo getAddressInfoFromKakaoAPI(String fullAddress) {
        JsonNode response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", fullAddress)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response != null && response.has("documents") && !response.get("documents").isEmpty()) {
            JsonNode document = response.get("documents").get(0);
            JsonNode addressNode = document.get("address");

            // 지번 주소가 있는 경우, 지번 주소를 사용
            String customerAddress = addressNode != null ?
                    addressNode.get("address_name").asText() : document.get("address_name").asText();

            double latitude = document.get("y").asDouble();  // 위도
            double longitude = document.get("x").asDouble();  // 경도

            return AddressInfo.builder()
                    .customerAddress(customerAddress)
                    .lat(latitude)
                    .lon(longitude)
                    .build();
        } else {
            throw new TransportOrderException(TransportOrderErrorCode.NOT_FOUND_ADDRESS);
        }
    }
}

