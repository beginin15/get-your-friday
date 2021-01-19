package com.toy.getyourfriday.component;

import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.Products;
import com.toy.getyourfriday.service.UpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.toy.getyourfriday.domain.ProductsTest.createProductsByLinks;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {ModelUrlParser.class})
class ProductContainerTest {

    private static final String MODEL_NAME = "lassie";

    @Autowired
    private ModelUrlParser modelUrlParser;

    @MockBean
    private UpdateService updateService;

    private ModelUrl modelUrl;
    private Products products;
    private ProductContainer actual;

    @BeforeEach
    void setUp() {
        this.modelUrl = modelUrlParser.findByName(MODEL_NAME);
        this.products = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300",
                "https://www.freitag.ch/ko/f11?productID=1135801"
        );
        this.actual = ProductContainer.of(modelUrl, products, updateService);
    }

    @Test
    @DisplayName("최초 업데이트인 경우")
    void checkUpdateAtFirstTime() {
        // given
        ProductContainer actual = new ProductContainer(updateService);

        // when
        actual.updateIfChanged(modelUrl, products);

        // then
        ProductContainer unexpected = new ProductContainer(updateService);
        assertThat(actual).isNotEqualTo(unexpected);
        verify(updateService, never()).update(modelUrl, products);
    }

    @Test
    @DisplayName("업데이트, 판매 모두 없음")
    void checkUpdateWhenNotUpdatedAndNotSold() {
        // when
        actual.updateIfChanged(modelUrl, products);

        // then
        ProductContainer expected = ProductContainer.of(modelUrl, products, updateService);
        assertThat(actual).isEqualTo(expected);
        verify(updateService, never()).update(modelUrl, products);
    }

    @Test
    @DisplayName("업데이트 개수와 판매 개수가 동일 - 업데이트")
    void checkUpdateWhenTotalSame() {
        // given
        Products latestProducts = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300",
                "https://www.freitag.ch/ko/f11?productID=1135753"
        );
        Products updatedProducts = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1135753"
        );

        doNothing().when(updateService).update(modelUrl, updatedProducts);

        // when
        actual.updateIfChanged(modelUrl, latestProducts);

        // then
        ProductContainer expected = ProductContainer.of(modelUrl, latestProducts, updateService);
        assertThat(actual).isEqualTo(expected);
        verify(updateService).update(modelUrl, updatedProducts);
    }

    @Test
    @DisplayName("업데이트 개수가 더 많음 - 업데이트")
    void checkUpdateWhenUpdatedMore() {
        // given
        Products latestProducts = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300",
                "https://www.freitag.ch/ko/f11?productID=1135801",
                "https://www.freitag.ch/ko/f11?productID=1135753"
        );
        Products updatedProducts = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1135753"
        );

        doNothing().when(updateService).update(modelUrl, updatedProducts);

        // when
        actual.updateIfChanged(modelUrl, latestProducts);

        // then
        ProductContainer expected = ProductContainer.of(modelUrl, latestProducts, updateService);
        assertThat(actual).isEqualTo(expected);
        verify(updateService).update(modelUrl, updatedProducts);
    }

    @Test
    @DisplayName("판매만 발생")
    void checkUpdateWhenOnlySold() {
        // given
        Products latestProducts = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300"
        );

        // when
        actual.updateIfChanged(modelUrl, latestProducts);

        // then
        ProductContainer expected = ProductContainer.of(modelUrl, latestProducts, updateService);
        assertThat(actual).isEqualTo(expected);
        verify(updateService, never()).update(modelUrl, latestProducts);
    }

    @Test
    @DisplayName("업데이트 개수보다 판매 개수가 더 많은 경우")
    void checkUpdatedWhenSoldMore() {
        // given
        Products latestProducts, updatedProducts;
        latestProducts = updatedProducts = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143322"
        );

        // when
        actual.updateIfChanged(modelUrl, latestProducts);

        // then
        ProductContainer expected = ProductContainer.of(modelUrl, latestProducts, updateService);
        assertThat(actual).isEqualTo(expected);
        verify(updateService).update(modelUrl, updatedProducts);
    }
}
