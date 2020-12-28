package com.toy.getyourfriday.component;

import com.google.common.collect.ImmutableList;
import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.Product;
import com.toy.getyourfriday.domain.Products;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(classes = ModelUrlParser.class)
class ProductContainerTest {

    private static final String MODEL_NAME = "lassie";
    private static final Products products;

    static {
        products = new Products(ImmutableList.of(
                new Product("https://www.freitag.ch/ko/f11?productID=1143300"),
                new Product("https://www.freitag.ch/ko/f11?productID=1135801"))
        );
    }

    @Autowired
    private ModelUrlParser modelUrlParser;

    @Test
    @DisplayName("최초 업데이트인 경우")
    void checkUpdate_first() {
        // given
        ProductContainer actual = new ProductContainer();

        // when
        actual.checkUpdate(modelUrlParser.findByName(MODEL_NAME), products);

        // then
        ProductContainer unexpected = new ProductContainer();
        assertNotEquals(unexpected, actual);
    }

    @Test
    @DisplayName("업데이트할 내용이 없는 경우")
    void checkUpdate_noUpdates() {
        // given
        ProductContainer actual = new ProductContainer();

        // when
        actual.checkUpdate(modelUrlParser.findByName(MODEL_NAME), products);

        // then
        Map<ModelUrl, Products> map = new HashMap<>();
        map.put(modelUrlParser.findByName(MODEL_NAME), products);
        ProductContainer expected = ProductContainer.of(map);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("업데이트된 경우")
    void checkUpdate() {
        // given
        Map<ModelUrl, Products> map = new HashMap<>();
        map.put(modelUrlParser.findByName(MODEL_NAME), products);
        ProductContainer actual = ProductContainer.of(map);

        // when
        Products updatedProducts = new Products(ImmutableList.of(
                new Product("https://www.freitag.ch/ko/f11?productID=1143300"),
                new Product("https://www.freitag.ch/ko/f11?productID=1135801"),
                new Product("https://www.freitag.ch/ko/f11?productID=1135753"))
        );
        actual.checkUpdate(modelUrlParser.findByName(MODEL_NAME), updatedProducts);

        // then
        ProductContainer unexpected = ProductContainer.of(map);
        assertNotEquals(unexpected, actual);
    }
}
