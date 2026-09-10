package com.customersummary;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public final class LocalRunner {

    private LocalRunner() {
    }

    public static void main(String[] args) {
        Handler handler = new Handler();
        APIGatewayProxyResponseEvent response = handler.handleRequest(
                new APIGatewayProxyRequestEvent(),
                null);

        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Body:");
        System.out.println(response.getBody());
    }
}
