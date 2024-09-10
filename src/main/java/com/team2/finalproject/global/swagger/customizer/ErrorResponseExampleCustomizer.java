package com.team2.finalproject.global.swagger.customizer;

import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import com.team2.finalproject.global.exception.response.ErrorResponse;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ErrorResponseExampleCustomizer {
    public void generateErrorResponseExample(Operation operation, ErrorCode errorCode, String enumName) {
        ApiResponses responses = operation.getResponses();

        MediaType mediaType = new MediaType();
        Example example = generateExample(errorCode);
        mediaType.addExamples(enumName, example);
        Content content = generateContent(mediaType);
        ApiResponse apiResponse = generateApiResponse(content);

        responses.addApiResponse(Integer.toString(errorCode.getHttpStatus().value()), apiResponse);
    }

    public void generateErrorResponseExample(Operation operation, List<Map<ErrorCode, String>> errorCodeList) {
        ApiResponses responses = operation.getResponses();

        Map<Integer, List<Map<ErrorCode, String>>> errorCodeExampleByStatus = errorCodeList.stream()
                .collect(Collectors.groupingBy(map -> map.keySet().iterator().next().getHttpStatus().value()));

        errorCodeExampleByStatus
                .forEach((statusCode, list) -> {
                    MediaType mediaType = new MediaType();
                    list.forEach(map ->
                            map.forEach((errorCode, enumName) -> {
                                Example example = generateExample(errorCode);
                                mediaType.addExamples(enumName, example);
                            }));
                    Content content = generateContent(mediaType);
                    ApiResponse apiResponse = generateApiResponse(content);
                    responses.addApiResponse(statusCode.toString(), apiResponse);
                });
    }

    private ApiResponse generateApiResponse(Content content) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setContent(content);
        return apiResponse;
    }

    private Content generateContent(MediaType mediaType) {
        Content content = new Content();
        content.addMediaType("application/json", mediaType);
        return content;
    }

    private Example generateExample(ErrorCode errorCode) {
        Example example = new Example();
        ErrorResponse errorResponse = ErrorResponse.from(errorCode);
        example.setValue(errorResponse);
        example.setDescription(errorCode.getMessage());
        return example;
    }
}