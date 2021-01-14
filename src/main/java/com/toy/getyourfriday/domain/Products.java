package com.toy.getyourfriday.domain;

import java.util.List;

public class Products {

    private final List<Product> products;

    public Products(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public boolean isUpdated(Products latest) {
        return products.size() < latest.size();
    }

    private int size() {
        return products.size();
    }
}
