package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.product.Products;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EqualsAndHashCode
@ToString
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

    public void updateIfChanged(ModelUrl modelUrl, Products latest) {
        if (!productsMap.containsKey(modelUrl)) {
            productsMap.put(modelUrl, latest);
            return;
        }
        if (isChanged(modelUrl, latest)) {
            productsMap.replace(modelUrl, latest);
        }
    }

    private boolean isChanged(ModelUrl modelUrl, Products latest) {
        Products previous = productsMap.get(modelUrl);
        if (latest.equals(previous)) {
            return false;
        }
        if (latest.isUpdated(previous)) {
            updateService.update(modelUrl, latest.getUpdatedProducts(previous));
            return true;
        }
        return true; // only sold
    }
}

