package com.toy.getyourfriday.domain.scraping;

import com.toy.getyourfriday.domain.product.Product;
import com.toy.getyourfriday.domain.product.Products;
import com.toy.getyourfriday.service.ProductContainer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class SeleniumScraper implements WebScraper {

    public static final By A_TAG_CSS_SELECTOR = By.cssSelector("ul.products-list > li > a");
    public static final By IMG_CSS_SELECTOR = By.cssSelector("img");
    public static final String A_TAG_ATTRIBUTE_NAME = "href";
    public static final String IMG_TAG_ATTRIBUTE_NAME = "src";

    private WebDriver driver;
    private final ChromeOptions options;
    private final ModelUrl modelUrl;
    private final ProductContainer productContainer;

    private SeleniumScraper(ChromeOptions options, ModelUrl modelUrl, ProductContainer productContainer) {
        this.options = options;
        this.modelUrl = modelUrl;
        this.productContainer = productContainer;
    }

    public static SeleniumScraper of(ChromeOptions options, ModelUrl modelUrl, ProductContainer productContainer) {
        return new SeleniumScraper(options, modelUrl, productContainer);
    }

    @Override
    public void run() {
        try {
            scrape().ifPresent(products -> productContainer.updateIfChanged(modelUrl, products));
        } catch (WebDriverException | NoClassDefFoundError e) {
            System.out.println(e.getMessage());
        } catch (ExceptionInInitializerError e) {
            System.out.println("브라우저 강제 종료");
        }
    }

    @Override
    public Optional<Products> scrape() throws WebDriverException, NoClassDefFoundError, ExceptionInInitializerError {
        if (driver == null) {
            return Optional.empty();
        }
        driver.get(modelUrl.getUrl());
        return Optional.ofNullable(driver.findElements(A_TAG_CSS_SELECTOR)
                .stream()
                .map(this::mapToProduct)
                .collect(collectingAndThen(toList(), Products::new)));
    }

    private Product mapToProduct(WebElement element) {
        return new Product(element.getAttribute(A_TAG_ATTRIBUTE_NAME),
                element.findElement(IMG_CSS_SELECTOR).getAttribute(IMG_TAG_ATTRIBUTE_NAME));
    }

    @Override
    public void start() {
        // WebDriver 인스턴스 생성할 때 리소스를 많이 사용하므로
        // 외부에서 생성 준비를 마쳤을 때 해당 메소드를 호출하여 비로소 인스턴스를 생성한다.
        this.driver = new ChromeDriver(options);
    }

    @Override
    public void quit() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }

    /*
        동등성 판단 기준에는 ModelUrl만 포함된다.
        WebDriver는 동등성을 직접 구현할 수 없기 때문에 인스턴스마다 다를 수 밖에 없으므로 비교 대상에 포함되기 힘들다.
        ProductContainer는 싱글톤 빈이기 때문에 실시간으로 내부 데이터가 변경(업데이트)될 수 있기 때문에 비교 대상에 포함되기 힘들다.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeleniumScraper scraper = (SeleniumScraper) o;
        return modelUrl.equals(scraper.modelUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelUrl);
    }
}
