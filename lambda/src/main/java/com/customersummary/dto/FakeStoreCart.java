package com.customersummary.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FakeStoreCart {
    private int id;
    private int userId;
    private String date;
    private List<FakeStoreCartItem> products;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<FakeStoreCartItem> getProducts() {
        return products;
    }

    public void setProducts(List<FakeStoreCartItem> products) {
        this.products = products;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FakeStoreCartItem {
        private int productId;
        private int quantity;

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
