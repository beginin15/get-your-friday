package com.toy.getyourfriday.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Products {

    private final List<Product> products;

    public boolean isUpdated(Products previous) {
        if (this.equals(previous)) { // 무조건 false
            return false;
        }
        if (this.products.size() > previous.size()) { // 무조건 true
            return true;
        }
        return isNewProductExisting(previous);
    }

    private int size() {
        return this.products.size();
    }

    private boolean isNewProductExisting(Products previous) {
        return this.products.stream().anyMatch(p -> !previous.containsProduct(p));
    }

    public Products getUpdatedProducts(Products previous) {
        return products.stream()
                .filter(p -> !previous.containsProduct(p))
                .collect(collectingAndThen(toList(), Products::new));
    }

    private boolean containsProduct(Product product) {
        return products.contains(product);
    }
}
