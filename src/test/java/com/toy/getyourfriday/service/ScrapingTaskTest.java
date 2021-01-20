package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.config.ApplicationConfig;
import com.toy.getyourfriday.domain.WebScraper;
import com.toy.getyourfriday.service.ScrapingManager.ScrapingTask;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(ApplicationConfig.class)
@SpringBootTest(classes = {
        ProductContainer.class,
        ModelUrlParser.class
})
class ScrapingTaskTest {

    private static final String MODEL_NAME = "lassie";

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ProductContainer productContainer;

    @Autowired
    private ModelUrlParser modelUrlParser;

    @Test
    @DisplayName("스케줄링 작업 취소와 드라이버 종료")
    void cancel() throws InterruptedException {
        // given
        PeriodicTrigger trigger = new PeriodicTrigger(10, TimeUnit.SECONDS);
        WebScraper scraper = WebScraper.of(
                new ChromeDriver(new ChromeOptions()),
                modelUrlParser.findByName(MODEL_NAME),
                productContainer
        );
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(scraper, trigger);
        ScrapingTask task = new ScrapingTask(scheduledFuture, scraper);
        Thread.sleep(3_000);

        // when
        task.cancel(true);

        // then
        assertTrue(scheduledFuture.isCancelled());
    }
}
