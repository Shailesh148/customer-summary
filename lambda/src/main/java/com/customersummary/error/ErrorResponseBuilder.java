package com.customersummary.error;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public final class ErrorResponseBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Map<String, String> JSON_HEADERS = Map.of("Content-Type", "application/json");

    private ErrorResponseBuilder() {
    }

    public static APIGatewayProxyResponseEvent build(int statusCode, String message, String code) {
        String body;
        try {
            body = OBJECT_MAPPER.writeValueAsString(Map.of("message", message, "code", code));
        } catch (JsonProcessingException e) {
            body = "{\"message\":\"failed to serialize error response\",\"code\":\"DOWNSTREAM_ERROR\"}";
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(JSON_HEADERS)
                .withBody(body);
    }
}
