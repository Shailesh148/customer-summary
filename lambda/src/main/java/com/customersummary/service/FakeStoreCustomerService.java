package com.customersummary.service;

import com.customersummary.client.FakeStoreHttpClient;
import com.customersummary.dto.CustomerSummary;
import com.customersummary.dto.FakeStoreCart;
import com.customersummary.dto.FakeStoreCustomer;
import com.customersummary.dto.FakeStoreProduct;
import com.customersummary.mapper.CustomerMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class FakeStoreCustomerService {

    private static final String FAKE_STORE_USERS_URL = "https://fakestoreapi.com/users";
    private static final String FAKE_STORE_PRODUCTS_URL = "https://fakestoreapi.com/products";
    private static final String FAKE_STORE_CARTS_URL = "https://fakestoreapi.com/carts";

    public List<CustomerSummary> fetchFakeStoreCustomerSummary() throws IOException, InterruptedException {
        CompletableFuture<List<FakeStoreCustomer>> usersFuture = async(this::fetchCustomerData);
        CompletableFuture<List<FakeStoreProduct>> productsFuture = async(this::fetchProductsData);
        CompletableFuture<List<FakeStoreCart>> cartsFuture = async(this::fetchCartsData);
        try {
            CompletableFuture.allOf(usersFuture, productsFuture, cartsFuture).get();

            return CustomerMapper.toCustomerSummaries(
                    usersFuture.get(),
                    productsFuture.get(),
                    cartsFuture.get());
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {

            Throwable cause = rootCause(e.getCause());
            if (cause instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("failed to fetch downstream data", cause);
        }
    }

    private static Throwable rootCause(Throwable error) {
        Throwable cause = error;
        while (cause instanceof CompletionException || cause instanceof ExecutionException) {
            cause = cause.getCause();
        }
        return cause;
    }

    private static <T> CompletableFuture<T> async(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    public List<FakeStoreCustomer> fetchCustomerData() throws IOException, InterruptedException {
        return FakeStoreHttpClient.getJson(FAKE_STORE_USERS_URL, new TypeReference<List<FakeStoreCustomer>>() {
        });
    }

    public List<FakeStoreProduct> fetchProductsData() throws IOException, InterruptedException {
        return FakeStoreHttpClient.getJson(FAKE_STORE_PRODUCTS_URL, new TypeReference<List<FakeStoreProduct>>() {
        });
    }

    public List<FakeStoreCart> fetchCartsData() throws IOException, InterruptedException {
        return FakeStoreHttpClient.getJson(FAKE_STORE_CARTS_URL, new TypeReference<List<FakeStoreCart>>() {
        });
    }
}
