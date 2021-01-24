package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import com.toy.getyourfriday.domain.scraping.WebScraper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ScrapingManager {

    public static final int SCHEDULE_PERIOD = 40;
    public static final int INITIAL_DELAY = 10;
    private static final PeriodicTrigger trigger;

    static {
        trigger = new PeriodicTrigger(SCHEDULE_PERIOD, TimeUnit.SECONDS);
        trigger.setInitialDelay(INITIAL_DELAY);
    }

    private final TaskScheduler taskScheduler;
    private final ProductContainer productContainer;
    private final ChromeOptions chromeOptions;

    private final Map<ModelUrl, ScrapingTask> scheduledTasks = new ConcurrentHashMap<>();

    @Autowired
    public ScrapingManager(TaskScheduler taskScheduler,
                           ProductContainer productContainer,
                           ChromeOptions chromeOptions) {
        this.taskScheduler = taskScheduler;
        this.productContainer = productContainer;
        this.chromeOptions = chromeOptions;
    }

    public void registerIfNotExist(ModelUrl modelUrl) {
        if (!containsModelUrl(modelUrl)) {
            register(modelUrl);
        }
    }

    private void register(ModelUrl modelUrl) {
        ScrapingTask scrapingTask = makeSchedule(modelUrl);
        if (putIfAbsent(modelUrl, scrapingTask)) {
            scrapingTask.start(new ChromeDriver(chromeOptions));
            return;
        }
        scrapingTask.cancel(true);
    }

    private ScrapingTask makeSchedule(ModelUrl modelUrl) {
        WebScraper scraper = WebScraper.of(
                modelUrl,
                productContainer
        );
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(scraper, trigger);
        return new ScrapingTask(scheduledTask, scraper);
    }

    private boolean putIfAbsent(ModelUrl modelUrl, ScrapingTask scrapingTask) {
        Optional<ScrapingTask> optional = Optional.ofNullable(scheduledTasks.putIfAbsent(modelUrl, scrapingTask));
        if (optional.isPresent()) {
            return !optional.map(v -> v.equals(scrapingTask)).get();
        }
        return true;
    }

    public void remove(ModelUrl modelUrl) {
        scheduledTasks.get(modelUrl).cancel(true);
        scheduledTasks.remove(modelUrl);
    }

    public boolean containsModelUrl(ModelUrl modelUrl) {
        return scheduledTasks.containsKey(modelUrl);
    }

    @PreDestroy
    public void destroy() {
        scheduledTasks.forEach((k, v) -> v.cancel(true));
    }

    /*
        동등성 판단 기준에는 scheduledTasks만 포함된다.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScrapingManager manager = (ScrapingManager) o;
        return scheduledTasks.equals(manager.scheduledTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduledTasks);
    }

    static public class ScrapingTask {

        private final ScheduledFuture<?> task;
        private final WebScraper scraper;

        public ScrapingTask(ScheduledFuture<?> task, WebScraper scraper) {
            this.task = task;
            this.scraper = scraper;
        }

        public void start(WebDriver webDriver) {
            scraper.setDriver(webDriver);
        }

        public void cancel(boolean mayInterruptIfRunning) {
            task.cancel(mayInterruptIfRunning);
            scraper.quitDriver();
        }

        /*
            동등성 판단 기준에는 WebScraper만 포함된다.
            ScheduledFuture는 TaskScheduler에 의해 생성되므로 비교 대상에 포함되기 힘들다.
         */

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScrapingTask that = (ScrapingTask) o;
            return scraper.equals(that.scraper);
        }

        @Override
        public int hashCode() {
            return Objects.hash(scraper);
        }
    }
}
