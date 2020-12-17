package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.Product;
import com.toy.getyourfriday.domain.Products;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Scraping implements Runnable {

    public static final By CSS_SELECTOR = By.cssSelector("ul.products-list > li > a");
    public static final String ATTRIBUTE_NAME = "href";

    private final WebDriver driver;
    private final String url;
    private final ProductContainer productContainer;

    public Scraping(WebDriver driver, String url, ProductContainer productContainer) {
        this.driver = driver;
        this.url = url;
        this.productContainer = productContainer;
    }

    @Override
    public void run() {
        driver.get(url);
        Products products = driver.findElements(CSS_SELECTOR)
                .stream()
                .map(e -> new Product(e.getAttribute(ATTRIBUTE_NAME)))
                .collect(collectingAndThen(toList(), Products::new));
        productContainer.checkUpdate(url, products);
    }
}
