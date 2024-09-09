package com.team2.finalproject.global.swagger.customizer;

import com.team2.finalproject.global.annotation.ApiErrorResponse;
import com.team2.finalproject.global.annotation.ApiErrorResponses;
import com.team2.finalproject.global.exception.errorcode.ErrorCode;
import io.swagger.v3.oas.models.Operation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOperationCustomizer implements OperationCustomizer {

    private final ErrorResponseExampleCustomizer errorResponseExampleCustomizer;

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiErrorResponse apiErrorResponseAnnotation = handlerMethod.getMethodAnnotation(ApiErrorResponse.class);
        ApiErrorResponses apiErrorResponsesAnnotation = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);

        if (apiErrorResponseAnnotation != null) {
            generateErrorResponse(operation, apiErrorResponseAnnotation);
        }

        if (apiErrorResponsesAnnotation != null) {
            generateErrorResponses(operation, apiErrorResponsesAnnotation);
        }

        return operation;
    }

    private void generateErrorResponse(Operation operation, ApiErrorResponse apiErrorResponseAnnotation) {
        ErrorCode errorCode = getErrorCodeByApiErrorResponse(apiErrorResponseAnnotation);
        String enumName = apiErrorResponseAnnotation.enumName();
        errorResponseExampleCustomizer.generateErrorResponseExample(operation, errorCode, enumName);
    }

    private void generateErrorResponses(Operation operation, ApiErrorResponses apiErrorResponsesAnnotation) {
        List<Map<ErrorCode, String>> errorCodeList = Arrays.stream(apiErrorResponsesAnnotation.value())
                .map(apiErrorResponseAnnotation -> {
                    ErrorCode errorCode = getErrorCodeByApiErrorResponse(apiErrorResponseAnnotation);
                    if (errorCode != null) {
                        return Map.of(errorCode, apiErrorResponseAnnotation.enumName());
                    }
                    return null;
                })
                .toList();

        errorResponseExampleCustomizer.generateErrorResponseExample(operation, errorCodeList);
    }

    private ErrorCode getErrorCodeByApiErrorResponse(ApiErrorResponse apiErrorResponseAnnotation) {
        Class<? extends ErrorCode> errorCodeClass = apiErrorResponseAnnotation.value();
        String enumName = apiErrorResponseAnnotation.enumName();

        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) errorCodeClass;

        return getEnumConstant(enumClass, enumName);
    }

    private <E extends Enum<E> & ErrorCode> ErrorCode getEnumConstant(Class<? extends Enum<?>> enumClass,
                                                                      String enumName) {
        try {
            // 제네릭 타입으로 맞춰 Enum 상수 가져오기
            @SuppressWarnings("unchecked")
            Class<E> specificEnumClass = (Class<E>) enumClass;
            return Enum.valueOf(specificEnumClass, enumName);
        } catch (ClassCastException | IllegalArgumentException e) {
            return null;
        }
    }
}
