package com.toy.getyourfriday.domain;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.config.ApplicationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.TimeUnit;

@SpringJUnitConfig(ApplicationConfig.class)
@SpringBootTest(classes = ModelUrlParser.class)
class WebScraperTest {

    public static final String MODEL_NAME = "lassie";

    @Autowired
    private ModelUrlParser urlParser;

    @Autowired
    private TaskScheduler taskScheduler;

    @MockBean
    private ProductContainer productContainer;

    @Test
    @DisplayName("스케줄링에 의한 스크래핑 테스트")
    void scrapingByScheduler() throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        WebScraper webScraper = new WebScraper(driver, urlParser.findByName(MODEL_NAME), productContainer);

        PeriodicTrigger trigger = new PeriodicTrigger(10, TimeUnit.SECONDS);
        trigger.setInitialDelay(10);
        taskScheduler.schedule(webScraper, trigger);

        Thread.sleep(100000);

        driver.close();
    }
}
