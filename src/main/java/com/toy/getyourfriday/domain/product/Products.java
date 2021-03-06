package com.toy.getyourfriday.domain.product;

import com.toy.getyourfriday.domain.user.User;
import com.toy.getyourfriday.response.UpdateResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Products {

    private final List<Product> products;

    public static Products of(Product... products) {
        return new Products(Arrays.asList(products.clone()));
    }

    public boolean isUpdated(Products previous) {
        if (this.products.size() > previous.size()) { // update more
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

    public List<UpdateResponse> responses(User user) {
        return products.stream()
                .map(p -> UpdateResponse.of(user, p))
                .collect(Collectors.toList());
    }
}
