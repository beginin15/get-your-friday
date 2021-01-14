package com.toy.getyourfriday.component;

import com.google.common.collect.ImmutableList;
import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.Product;
import com.toy.getyourfriday.domain.Products;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ModelUrlParser.class})
class ProductContainerTest {

    private static final String MODEL_NAME = "lassie";

    @Autowired
    private ModelUrlParser modelUrlParser;

    private ModelUrl modelUrl;
    private Products products;

    @BeforeEach
    void setUp() {
        this.modelUrl = modelUrlParser.findByName(MODEL_NAME);
        this.products = new Products(ImmutableList.of(
                new Product("https://www.freitag.ch/ko/f11?productID=1143300"),
                new Product("https://www.freitag.ch/ko/f11?productID=1135801"))
        );
    }

    @Test
    @DisplayName("최초 업데이트인 경우")
    void checkUpdateWhenFirstTime() {
        // given
        ProductContainer actual = new ProductContainer();

        // when
        actual.checkUpdate(modelUrl, products);

        // then
        ProductContainer unexpected = new ProductContainer();
        assertThat(actual).isNotEqualTo(unexpected);
    }

    @Test
    @DisplayName("업데이트할 내용이 없는 경우")
    void checkUpdateWhenNotUpdated() {
        // given
        ProductContainer actual = ProductContainer.of(modelUrl, products);

        // when
        actual.checkUpdate(modelUrl, products);

        // then
        ProductContainer expected = ProductContainer.of(modelUrl, products);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("업데이트된 경우")
    void checkUpdate() {
        // given
        ProductContainer actual = ProductContainer.of(modelUrl, products);
        Products updatedProducts = new Products(ImmutableList.of(
                new Product("https://www.freitag.ch/ko/f11?productID=1143300"),
                new Product("https://www.freitag.ch/ko/f11?productID=1135801"),
                new Product("https://www.freitag.ch/ko/f11?productID=1135753"))
        );

        // when
        actual.checkUpdate(modelUrl, updatedProducts);

        // then
        ProductContainer unexpected = ProductContainer.of(modelUrl, products);
        assertThat(actual).isNotEqualTo(unexpected);
        assertThat(products.isUpdated(updatedProducts)).isEqualTo(true);
    }

    @Test
    @DisplayName("제품이 판매된 경우 (업데이트x)")
    void checkUpdateWhenProductSold() {
        // given
        ProductContainer actual = ProductContainer.of(modelUrl, products);
        Products remainedProducts = new Products(ImmutableList.of(
                new Product("https://www.freitag.ch/ko/f11?productID=1143300"))
        );

        // when
        actual.checkUpdate(modelUrl, remainedProducts);

        // then
        ProductContainer unexpected = ProductContainer.of(modelUrl, products);
        assertThat(actual).isNotEqualTo(unexpected);
        assertThat(products.isUpdated(remainedProducts)).isEqualTo(false);
    }
}
