package com.toy.getyourfriday.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductsTest {

    private Products previous;
    private Product existingA;
    private Product existingB;
    private Product updated;

    @BeforeEach
    void setUp() {
        this.existingA = new Product("https://www.freitag.ch/ko/f11?productID=1143300",
                "https://freitag.rokka.io/neo_square_thumbnail/abc/0000-1.jpg");
        this.existingB = new Product("https://www.freitag.ch/ko/f11?productID=1135801",
                "https://freitag.rokka.io/neo_square_thumbnail/abc/0000-2.jpg");
        this.updated = new Product("https://www.freitag.ch/ko/f11?productID=1135822",
                "https://freitag.rokka.io/neo_square_thumbnail/abc/0000-3.jpg");
        this.previous = Products.of(this.existingA, this.existingB);
    }

    @Test
    @DisplayName("업데이트, 판매 모두 없음")
    void isUpdatedWhenNothing() {
        Products latest = Products.of(existingA, existingB);
        assertThat(latest.isUpdated(previous)).isFalse();
    }

    @Test
    @DisplayName("업데이트 개수와 판매 개수가 동일 - 업데이트")
    void isUpdatedWhenTotalSame() {
        Products latest = Products.of(existingA, updated);
        assertThat(latest.isUpdated(previous)).isTrue();
    }

    @Test
    @DisplayName("업데이트 개수가 더 많음 - 업데이트")
    void isUpdatedWhenUpdatedMore() {
        Products latest = Products.of(existingA, existingB, updated);
        assertThat(latest.isUpdated(previous)).isTrue();
    }

    @Test
    @DisplayName("판매만 발생")
    void isUpdatedWhenOnlySold() {
        Products latest = Products.of(existingA);
        assertThat(latest.isUpdated(previous)).isFalse();
    }

    @Test
    @DisplayName("업데이트 개수보다 판매 개수가 더 많은 경우")
    void isUpdatedWhenSoldMore() {
        Products latest = Products.of(updated);
        assertThat(latest.isUpdated(previous)).isTrue();
    }

    @Test
    @DisplayName("업데이트된 제품들만 분류")
    void getUpdatedProducts() {
        // given
        Products latest = Products.of(existingA, existingB, updated);

        // when
        Products updatedProducts = latest.getUpdatedProducts(previous);

        // then
        assertThat(updatedProducts).isEqualTo(Products.of(updated));
    }
}
