package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.config.ApplicationConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.toy.getyourfriday.service.ScrapingTask.*;
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
    void cancel() throws InterruptedException {
        // given
        PeriodicTrigger trigger = new PeriodicTrigger(10, TimeUnit.SECONDS);
        ScraperWrapper wrapper = ScraperWrapper.of(
                new ChromeDriver(), modelUrlParser.findByName(MODEL_NAME), productContainer
        );
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(wrapper, trigger);
        ScrapingTask task = new ScrapingTask(scheduledFuture, wrapper);
        Thread.sleep(3000);

        // when
        task.cancel(true);

        // then
        assertTrue(scheduledFuture.isCancelled());
    }
}
