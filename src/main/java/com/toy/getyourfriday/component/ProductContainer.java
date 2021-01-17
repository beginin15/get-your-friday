package com.toy.getyourfriday.component;

import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.Products;
import com.toy.getyourfriday.service.UpdateService;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EqualsAndHashCode
public class ProductContainer {

    private Map<ModelUrl, Products> productsMap = new ConcurrentHashMap<>();
    private final UpdateService updateService;

    @Autowired
    public ProductContainer(UpdateService updateService) {
        this.updateService = updateService;
    }

    private ProductContainer(Map<ModelUrl, Products> productsMap, UpdateService updateService) {
        this.productsMap = productsMap;
        this.updateService = updateService;
    }

    public static ProductContainer of(ModelUrl modelUrl,
                                      Products products,
                                      UpdateService updateService) {
        Map<ModelUrl, Products> map = new HashMap<>();
        map.put(modelUrl, products);
        return new ProductContainer(map, updateService);
    }

    public void checkUpdate(ModelUrl modelUrl, Products products) {
        if (productsMap.containsKey(modelUrl)) {
            updateIfChanged(modelUrl, products);
            return;
        }
        productsMap.put(modelUrl, products);
    }

    private void updateIfChanged(ModelUrl modelUrl, Products latest) {
        Products previous = productsMap.get(modelUrl);
        if (latest.isUpdated(previous)) {
            updateService.update(modelUrl, latest.getUpdatedProducts(previous));
            productsMap.remove(modelUrl, latest);
        }
    }
}

