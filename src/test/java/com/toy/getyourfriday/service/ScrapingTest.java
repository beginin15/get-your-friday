package com.toy.getyourfriday.service;

import com.toy.getyourfriday.config.ApplicationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

@SpringJUnitConfig(ApplicationConfig.class)
class ScrapingTest {

    public static final String TEST_URL = "https://www.freitag.ch/en/f11?items=showall";

    @Autowired
    private TaskScheduler taskScheduler;

    @MockBean
    private ProductContainer updateHandler;

    @Test
    @DisplayName("스케줄링에 의한 스크래핑 테스트")
    void scrapingByScheduler() throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        Scraping scraping = new Scraping(driver, TEST_URL, updateHandler);

        PeriodicTrigger trigger = new PeriodicTrigger(10, TimeUnit.SECONDS);
        trigger.setInitialDelay(10);
        taskScheduler.schedule(scraping, trigger);

        Thread.sleep(35000);

        driver.close();
    }
}
