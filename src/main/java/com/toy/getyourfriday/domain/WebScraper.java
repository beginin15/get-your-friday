package com.toy.getyourfriday.domain;

import com.toy.getyourfriday.component.ProductContainer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class WebScraper implements Runnable {

    public static final By CSS_SELECTOR = By.cssSelector("ul.products-list > li > a");
    public static final String ATTRIBUTE_NAME = "href";

    private final WebDriver driver;
    private final ModelUrl modelUrl;
    private final ProductContainer productContainer;

    public WebScraper(WebDriver driver, ModelUrl url, ProductContainer productContainer) {
        this.driver = driver;
        this.modelUrl = url;
        this.productContainer = productContainer;
    }

    @Override
    public void run() {
        driver.get(modelUrl.getUrl());
        Products products = driver.findElements(CSS_SELECTOR)
                .stream()
                .map(e -> new Product(e.getAttribute(ATTRIBUTE_NAME)))
                .collect(collectingAndThen(toList(), Products::new));
        productContainer.checkUpdate(modelUrl, products);
    }

    public void quitDriver() {
        driver.quit();
    }
}
