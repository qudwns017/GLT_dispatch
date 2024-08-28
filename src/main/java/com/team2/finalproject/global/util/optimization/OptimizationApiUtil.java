package com.team2.finalproject.global.util.optimization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OptimizationApiUtil {

    private final WebClient webClient;

    public OptimizationApiUtil(@Value("${optimization-api.uri}")String uri){
        this.webClient = WebClient.builder().baseUrl(uri).build();
    }

    public OptimizationResponse getOptimizationResponse(OptimizationRequest request){
        return webClient.post()
                .uri("/api/OptimumPath")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OptimizationResponse.class)
                .block();
    }

}
