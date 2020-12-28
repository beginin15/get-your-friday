package com.toy.getyourfriday.component;

import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.Products;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProductContainer {

    private Map<ModelUrl, Products> productsMap = new ConcurrentHashMap<>();

    public static ProductContainer of(Map<ModelUrl, Products> productsMap) {
        ProductContainer productContainer = new ProductContainer();
        productContainer.productsMap = new HashMap<>(productsMap);
        return productContainer;
    }

    public void checkUpdate(ModelUrl modelUrl, Products products) {
        if (productsMap.containsKey(modelUrl)) {
            updateIfChanged(modelUrl, products);
            return;
        }
        productsMap.put(modelUrl, products);
    }

    private void updateIfChanged(ModelUrl modelUrl, Products latest) {
        if (!productsMap.get(modelUrl).equals(latest)) {
            // UpdateService 호출
            productsMap.replace(modelUrl, latest);
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
