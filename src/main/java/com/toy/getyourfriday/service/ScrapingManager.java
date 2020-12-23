package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.domain.ModelUrl;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.toy.getyourfriday.service.ScrapingTask.ScraperWrapper;

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

    public ScrapingManager(TaskScheduler taskScheduler,
                           ProductContainer productContainer,
                           ChromeOptions chromeOptions) {
        this.taskScheduler = taskScheduler;
        this.productContainer = productContainer;
        this.chromeOptions = chromeOptions;
    }

    public boolean register(ModelUrl modelUrl) {
        ScrapingTask scrapingTask = makeSchedule(modelUrl);
        Optional<ScrapingTask> returnValue = Optional.ofNullable(scheduledTasks.putIfAbsent(modelUrl, scrapingTask));
        try {
            checkDuplication(returnValue, scrapingTask);
        } catch (DuplicateKeyException e) {
            scrapingTask.cancel(true);
            return false;
        }
        return true;
    }

    private ScrapingTask makeSchedule(ModelUrl modelUrl) {
        ScraperWrapper wrapper = ScraperWrapper.of(new ChromeDriver(chromeOptions), modelUrl, productContainer);
        return new ScrapingTask(taskScheduler.schedule(wrapper, trigger), wrapper);
    }

    private void checkDuplication(Optional<ScrapingTask> optional,
                                  ScrapingTask scrapingTask) throws DuplicateKeyException {
        optional.ifPresent(t -> {
            if (!t.equals(scrapingTask)) {
                throw new DuplicateKeyException();
            }
        });
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

    private static class DuplicateKeyException extends RuntimeException {}
}
