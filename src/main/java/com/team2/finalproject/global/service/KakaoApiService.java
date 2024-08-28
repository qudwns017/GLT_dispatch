package com.team2.finalproject.global.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.team2.finalproject.global.util.response.AddressInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoApiService {

    private final WebClient.Builder webClientBuilder;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public KakaoApiService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public AddressInfo getAddressInfoFromKakaoAPI(String fullAddress) {
        WebClient webClient = webClientBuilder.build();
        String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json?query=" + fullAddress;

        JsonNode response = webClient.get()
                .uri(apiUrl)
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response != null && response.has("documents") && !response.get("documents").isEmpty()) {
            JsonNode document = response.get("documents").get(0);
            JsonNode addressNode = document.get("address");

            // 지번 주소가 있는 경우, 지번 주소를 사용
            String customerAddress = addressNode != null ? addressNode.get("address_name").asText() : document.get("address_name").asText();
            double latitude = document.get("y").asDouble();  // 위도
            double longitude = document.get("x").asDouble();  // 경도

            return AddressInfo.builder()
                    .customerAddress(customerAddress)
                    .lat(latitude)
                    .lon(longitude)
                    .build();
        }

        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Failed to kakao Api exception");
        }

        return null;
    }
}

