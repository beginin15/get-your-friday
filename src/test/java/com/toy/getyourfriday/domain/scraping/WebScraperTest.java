package com.toy.getyourfriday.domain.scraping;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.config.ApplicationConfig;
import com.toy.getyourfriday.service.ProductContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SpringJUnitConfig(ApplicationConfig.class)
@SpringBootTest(classes = {ModelUrlParser.class})
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
        // given
        WebScraper webScraper = WebScraper.of(
                new ChromeDriver(new ChromeOptions()),
                urlParser.findByName(MODEL_NAME),
                productContainer
        );

        final int period = 10;
        final int initialDelay = 10;
        PeriodicTrigger trigger = new PeriodicTrigger(period, TimeUnit.SECONDS);
        trigger.setInitialDelay(initialDelay);

        // when
        ScheduledFuture<?> task = taskScheduler.schedule(webScraper, trigger);

        Thread.sleep(100_000);

        task.cancel(true);
        webScraper.quitDriver();
    }
}
