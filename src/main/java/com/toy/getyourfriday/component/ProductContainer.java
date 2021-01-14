package com.toy.getyourfriday.component;

import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.Products;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductContainer {

    private final Map<ModelUrl, Products> productsMap = new ConcurrentHashMap<>();

    public static ProductContainer of(ModelUrl modelUrl, Products products) {
        Map<ModelUrl, Products> map = new HashMap<>();
        map.put(modelUrl, products);
        return new ProductContainer(map);
    }

    public void checkUpdate(ModelUrl modelUrl, Products products) {
        if (productsMap.containsKey(modelUrl)) {
            updateIfChanged(modelUrl, products);
            return;
        }
        productsMap.put(modelUrl, products);
    }

    private void updateIfChanged(ModelUrl modelUrl, Products latest) {
        Products origin = productsMap.get(modelUrl);
        if (origin.equals(latest)) {
            return;
        }
        if (origin.isUpdated(latest)) {
            // UpdateService 호출
        }
        productsMap.replace(modelUrl, latest);
    }
}
