package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.Products;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class ProductContainer {

    private Map<String, Products> productsMap = new HashMap<>();

    public static ProductContainer of(Map<String, Products> productsMap) {
        ProductContainer productContainer = new ProductContainer();
        productContainer.productsMap = new HashMap<>(productsMap);
        return productContainer;
    }

    public void checkUpdate(String productUrl, Products products) {
        if (productsMap.containsKey(productUrl)) {
            updateIfChanged(productUrl, products);
            return;
        }
        productsMap.put(productUrl, products);
    }

    private void updateIfChanged(String productUrl, Products latest) {
        if (!productsMap.get(productUrl).equals(latest)) {
            // UpdateService 호출
            productsMap.replace(productUrl, latest);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductContainer that = (ProductContainer) o;
        return productsMap.equals(that.productsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productsMap);
    }
}
