package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.Product;
import com.toy.getyourfriday.domain.Products;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Scraping {

    private final WebDriver driver;
    private final String url;

    public Scraping(WebDriver driver, String url) {
        this.driver = driver;
        this.url = url;
    }

    public Products scraping() {
        driver.get(url);
        return driver.findElements(By.cssSelector("ul.products-list > li > a"))
                .stream()
                .map(e -> new Product(e.getAttribute("href")))
                .collect(collectingAndThen(toList(), Products::new));
    }
}
