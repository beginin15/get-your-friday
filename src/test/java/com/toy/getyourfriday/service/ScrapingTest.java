package com.toy.getyourfriday.service;

import com.toy.getyourfriday.config.ApplicationConfig;
import com.toy.getyourfriday.domain.Products;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(ApplicationConfig.class)
class ScrapingTest {

    @Autowired
    private ChromeOptions chromeOptions;

    @Test
    void scraping() {
        // given
        WebDriver driver = new ChromeDriver(chromeOptions);
        String url = "https://www.freitag.ch/en/f11?items=showall";
        Scraping scraping = new Scraping(driver, url);

        // when
        Products products = scraping.scraping();

        // then
        assertNotNull(products);
        assertNotEquals(0, products.getProducts().size());

        driver.close();
    }
}
