package com.customersummary.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

public class FakeStoreHttpClient {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private FakeStoreHttpClient() {
    }

    public static <T> T getJson(String url, TypeReference
        <T> typeReference)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0 (compatible; AWS-Lambda)")
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw e;
        } catch (IOException e) {
            throw new IOException("error while calling " + url + ": " + e.getMessage(), e);
        }

        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new IOException("request to " + url + " returned status " + status);
        }

        try {
            return OBJECT_MAPPER.readValue(response.body(), typeReference);
        } catch (IOException e) {
            throw new IOException("error while decoding data from " + url + ": " + e.getMessage(), e);
        }
    }
}
