package com.customersummary.mapper;

import com.customersummary.dto.CustomerSummary;
import com.customersummary.dto.FakeStoreCart;
import com.customersummary.dto.FakeStoreCustomer;
import com.customersummary.dto.FakeStoreProduct;
import com.customersummary.dto.LineItem;
import com.customersummary.dto.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapper {

    private CustomerMapper() {
    }

    public static List<CustomerSummary> toCustomerSummaries(
            List<FakeStoreCustomer> users, List<FakeStoreProduct> products, List<FakeStoreCart> carts) {

        Map<Integer, FakeStoreProduct> productsById = new HashMap<>();
        for (FakeStoreProduct product : products) {
            productsById.put(product.getId(), product);
        }

        Map<Integer, List<FakeStoreCart>> cartsByUserId = new HashMap<>();
        for (FakeStoreCart cart : carts) {
            cartsByUserId.computeIfAbsent(cart.getUserId(), k -> new ArrayList<>()).add(cart);
        }

        List<CustomerSummary> customers = new ArrayList<>();
        for (FakeStoreCustomer user : users) {
            List<FakeStoreCart> userCarts = cartsByUserId.get(user.getId());
            if (userCarts == null || userCarts.isEmpty()) {
                continue;
            }

            CustomerSummary customer = new CustomerSummary();
            customer.setUserId(user.getId());
            customer.setName(user.getName().getFirstname() + " " + user.getName().getLastname());
            customer.setEmail(user.getEmail());
            customer.setOrderCount(userCarts.size());

            List<Order> orders = new ArrayList<>();
            double grandTotal = 0;

            for (FakeStoreCart cart : userCarts) {
                Order order = new Order();
                order.setOrderId(cart.getId());
                order.setDate(cart.getDate());

                List<LineItem> items = new ArrayList<>();
                double orderTotal = 0;

                for (FakeStoreCart.FakeStoreCartItem cartItem : cart.getProducts()) {
                    LineItem item = new LineItem();
                    item.setProductId(cartItem.getProductId());
                    item.setQuantity(cartItem.getQuantity());

                    FakeStoreProduct product = productsById.get(cartItem.getProductId());
                    if (product == null) {
                        item.setUnresolved(true);
                        items.add(item);
                        continue;
                    }

                    item.setTitle(product.getTitle());
                    item.setCategory(product.getCategory());
                    item.setUnitPrice(product.getPrice());
                    double lineTotal = product.getPrice() * cartItem.getQuantity();
                    item.setLineTotal(lineTotal);
                    orderTotal += lineTotal;
                    items.add(item);
                }

                order.setItems(items);
                order.setOrderTotal(orderTotal);
                grandTotal += orderTotal;
                orders.add(order);
            }

            customer.setOrders(orders);
            customer.setGrandTotal(grandTotal);
            customers.add(customer);
        }

        return customers;
    }
}
