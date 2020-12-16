package com.toy.getyourfriday.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void create() {
        driver = createDriverByOptions(null, false);
        assertNotNull(driver);
    }

    @Test
    @DisplayName("UI 유무에 따른 성능 차이 테스트")
    public void headlessMode() {
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
    public void scrapingFreitag(){
        // given
        String url = "https://www.freitag.ch/en/f41?items=showall";
        driver = createDriverByOptions(null, true);

        // when
        driver.get(url);
        List<WebElement> elements = driver.findElements(By.cssSelector("ul.products-list > li > a"));

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

    private WebDriver createDriverByOptions(Proxy proxy, boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (proxy != null) {
            options.setCapability("proxy", proxy);
        }
        options.setHeadless(headless);
        return new RemoteWebDriver(driverService.getUrl(), options);
    }
}
