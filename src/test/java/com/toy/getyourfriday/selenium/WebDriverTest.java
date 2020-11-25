package com.toy.getyourfriday.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;

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
        driver.quit();
    }

    @Test
    @DisplayName("드라이버 생성 테스트")
    public void create() {
        driver = new RemoteWebDriver(driverService.getUrl(), new ChromeOptions());
        assertNotNull(driver);
    }

    @Test
    @DisplayName("UI 유무에 따른 성능 차이 테스트")
    public void headlessMode() {
        // when - UI 있는 경우
        long headMode = System.currentTimeMillis();
        driver = new RemoteWebDriver(driverService.getUrl(), new ChromeOptions());
        long resultOfheadMode = System.currentTimeMillis() - headMode;
        driver.quit();

        // when - UI 없는 경우
        long headlessMode = System.currentTimeMillis();
        driver = new RemoteWebDriver(driverService.getUrl(), new ChromeOptions().setHeadless(true));
        long resultOfheadless = System.currentTimeMillis() - headlessMode;

        // then
        assertTrue(resultOfheadless < resultOfheadMode);
    }
}
