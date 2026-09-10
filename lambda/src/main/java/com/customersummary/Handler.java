package com.customersummary;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.customersummary.dto.CustomerSummary;
import com.customersummary.error.ErrorResponseBuilder;
import com.customersummary.service.FakeStoreCustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.util.List;
import java.util.Map;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Map<String, String> JSON_HEADERS = Map.of("Content-Type", "application/json");

    private final FakeStoreCustomerService service;

    public Handler() {
        this(new FakeStoreCustomerService());
    }

    Handler(FakeStoreCustomerService service) {
        this.service = service;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            List<CustomerSummary> customerSummaryResponse = service.fetchFakeStoreCustomerSummary();
            String body = OBJECT_MAPPER.writeValueAsString(customerSummaryResponse);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(JSON_HEADERS)
                    .withBody(body);
        } catch (HttpTimeoutException e) {
            return ErrorResponseBuilder.build(
                    504, "failed to fetch customer summary: " + e.getMessage(), "DOWNSTREAM_TIMEOUT");
        } catch (Exception e) {
            return ErrorResponseBuilder.build(
                    502, "failed to fetch customer summary: " + e.getMessage(), "DOWNSTREAM_ERROR");
        }
    }
}
