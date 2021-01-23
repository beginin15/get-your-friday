package com.toy.getyourfriday.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.toy.getyourfriday.domain.WebScraper.A_TAG_ATTRIBUTE_NAME;
import static com.toy.getyourfriday.domain.WebScraper.IMG_CSS_SELECTOR;
import static com.toy.getyourfriday.domain.WebScraper.IMG_TAG_ATTRIBUTE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebDriverTest {

    private static ChromeDriverService driverService;
    private WebDriver driver;

    @BeforeAll
    public static void createAndStartService() throws IOException {
        driverService = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(System.getProperty("webdriver.chrome.driver")))
                .usingAnyFreePort()
                .build();
        driverService.start();
    }

    @AfterAll
    public static void stopService() {
        driverService.stop();
    }

    @AfterEach
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("드라이버 생성 테스트")
    void create() {
        driver = createDriverByOptions(null, false);
        assertNotNull(driver);
    }

    @Test
    @DisplayName("UI 유무에 따른 성능 차이 테스트")
    void headlessMode() {
        // when - UI 있는 경우
        long headMode = System.currentTimeMillis();
        driver = createDriverByOptions(null, false);
        long resultOfheadMode = System.currentTimeMillis() - headMode;
        driver.quit();

        // when - UI 없는 경우
        long headlessMode = System.currentTimeMillis();
        driver = createDriverByOptions(null, true);
        long resultOfheadless = System.currentTimeMillis() - headlessMode;

        // then
        System.out.println("before: " + resultOfheadMode + ", after: " + resultOfheadless);
        assertTrue(resultOfheadless < resultOfheadMode);
    }

    @Test
    @DisplayName("프라이탁 웹페이지 스크랩핑 테스트")
    void scrapingFreitag(){
        // given
        String url = "https://www.freitag.ch/en/f41?items=showall";
        driver = createDriverByOptions(null, true);

        // when
        driver.get(url);
        List<WebElement> elements = driver.findElements(By.cssSelector("ul.products-list > li > a"));
        elements.forEach(e -> {
            System.out.println(String.format("a href: %s, img src: %s",
                    e.getAttribute(A_TAG_ATTRIBUTE_NAME),
                    e.findElement(IMG_CSS_SELECTOR).getAttribute(IMG_TAG_ATTRIBUTE_NAME)));
        });

        // then
        System.out.println(elements.size());
        assertNotEquals(0, elements.size());
    }

    @Test
    @DisplayName("tor를 이용한 ip 주소 우회 확인하기")
    void bypassIpAddress () throws InterruptedException {
        // given
        Proxy proxy = new Proxy();
        proxy.setSslProxy("socks5://127.0.0.1:9050"); // tor 포트

        // when
        WebDriver driver1 = createDriverByOptions(proxy, true);
        driver1.get("https://ip.pe.kr/");
        String ipAddress1 = driver1.findElement(By.className("cover-heading")).getText();

        Thread.sleep(10000); // ip 변경 주기

        WebDriver driver2 = createDriverByOptions(proxy, true);
        driver2.get("https://ip.pe.kr/");
        String ipAddress2 = driver2.findElement(By.className("cover-heading")).getText();

        // then
        assertNotEquals(ipAddress1, ipAddress2);
    }

    @Test
    @RepeatedTest(value = 10)
    @DisplayName("WebDriver와 RemoteWebDriver 인스턴스 생성 시간 비교")
    void compareWithRemoteWebDriver() {
        long before = System.currentTimeMillis();
        WebDriver remoteWebDriver = new RemoteWebDriver(driverService.getUrl(), new ChromeOptions());
        long instanceTimeOfRemote = System.currentTimeMillis() - before;

        before = System.currentTimeMillis();
        WebDriver driverDefault = new ChromeDriver(new ChromeOptions());
        long instanceTimeOfDefault = System.currentTimeMillis() - before;

        assertThat(instanceTimeOfDefault > instanceTimeOfRemote).isTrue();
        System.out.println("Default: " + instanceTimeOfDefault + ", Remote: " + instanceTimeOfRemote);

        closeDriver(remoteWebDriver, driverDefault);
    }

    private WebDriver createDriverByOptions(Proxy proxy, boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (proxy != null) {
            options.setCapability("proxy", proxy);
        }
        options.setHeadless(headless);
        return new RemoteWebDriver(driverService.getUrl(), options);
    }

    private void closeDriver(WebDriver...drivers) {
        Arrays.stream(drivers).forEach(d -> {
            d.close();
            d.quit();
        });
    }
}
