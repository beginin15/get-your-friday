package com.toy.getyourfriday.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductsTest {

    private Products previous;

    @BeforeEach
    void setUp() {
        this.previous = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300",
                "https://www.freitag.ch/ko/f11?productID=1135801"
        );
    }

    @Test
    @DisplayName("업데이트, 판매 모두 없음")
    void isUpdatedWhenNothing() {
        Products latest = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300",
                "https://www.freitag.ch/ko/f11?productID=1135801"
        );
        assertThat(latest.isUpdated(previous)).isFalse();
    }

    @Test
    @DisplayName("업데이트 개수와 판매 개수가 동일 - 업데이트")
    void isUpdatedWhenTotalSame() {
        Products latest = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300",
                "https://www.freitag.ch/ko/f11?productID=1135822"
        );
        assertThat(latest.isUpdated(previous)).isTrue();
    }

    @Test
    @DisplayName("업데이트 개수가 더 많음 - 업데이트")
    void isUpdatedWhenUpdatedMore() {
        Products latest = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300",
                "https://www.freitag.ch/ko/f11?productID=1135801",
                "https://www.freitag.ch/ko/f11?productID=1135822"
        );
        assertThat(latest.isUpdated(previous)).isTrue();
    }

    @Test
    @DisplayName("판매만 발생")
    void isUpdatedWhenOnlySold() {
        Products latest = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300"
        );
        assertThat(latest.isUpdated(previous)).isFalse();
    }

    @Test
    @DisplayName("업데이트 개수보다 판매 개수가 더 많은 경우")
    void isUpdatedWhenSoldMore() {
        Products latest = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1135822"
        );
        assertThat(latest.isUpdated(previous)).isTrue();
    }

    @Test
    @DisplayName("업데이트된 제품들만 분류")
    void getUpdatedProducts() {
        // given
        Products latest = createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1143300",
                "https://www.freitag.ch/ko/f11?productID=1135801",
                "https://www.freitag.ch/ko/f11?productID=1135822"
        );

        // when
        Products updatedProducts = latest.getUpdatedProducts(previous);

        // then
        assertThat(updatedProducts).isEqualTo(createProductsByLinks(
                "https://www.freitag.ch/ko/f11?productID=1135822")
        );
    }

    public static Products createProductsByLinks(String...links) {
        List<Product> products = Arrays.stream(links)
                .map(Product::new)
                .collect(Collectors.toList());
        return new Products(products);
    }
}
