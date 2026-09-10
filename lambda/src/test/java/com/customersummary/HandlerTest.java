package com.customersummary;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.customersummary.dto.CustomerSummary;
import com.customersummary.service.FakeStoreCustomerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandlerTest {

    @Test
    void returns502ForDownstreamError() throws Exception {
        Handler handler = new Handler(failingService(new IOException("products service unavailable")));

        APIGatewayProxyResponseEvent response = handler.handleRequest(new APIGatewayProxyRequestEvent(), null);
        JsonNode body = new ObjectMapper().readTree(response.getBody());

        assertEquals(502, response.getStatusCode());
        assertEquals("DOWNSTREAM_ERROR", body.get("code").asText());
        assertEquals("failed to fetch customer summary: products service unavailable", body.get("message").asText());
        assertEquals("application/json", response.getHeaders().get("Content-Type"));
    }

    @Test
    void returns504ForDownstreamTimeout() throws Exception {
        Handler handler = new Handler(failingService(new HttpTimeoutException("products service timed out")));

        APIGatewayProxyResponseEvent response = handler.handleRequest(new APIGatewayProxyRequestEvent(), null);
        JsonNode body = new ObjectMapper().readTree(response.getBody());

        assertEquals(504, response.getStatusCode());
        assertEquals("DOWNSTREAM_TIMEOUT", body.get("code").asText());
        assertEquals("failed to fetch customer summary: products service timed out", body.get("message").asText());
    }

    @Test
    void returns200WithCustomerArrayOnSuccess() throws Exception {
        Handler handler = new Handler(new FakeStoreCustomerService() {
            @Override
            public List<CustomerSummary> fetchFakeStoreCustomerSummary() {
                return List.of();
            }
        });

        APIGatewayProxyResponseEvent response = handler.handleRequest(new APIGatewayProxyRequestEvent(), null);

        assertEquals(200, response.getStatusCode());
        assertEquals("[]", response.getBody());
    }

    private static FakeStoreCustomerService failingService(IOException failure) {
        return new FakeStoreCustomerService() {
            @Override
            public List<CustomerSummary> fetchFakeStoreCustomerSummary() throws IOException {
                throw failure;
            }
        };
    }
}
