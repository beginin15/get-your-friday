package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.WebScraper;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.ScheduledFuture;

public class ScrapingTask {

    private final ScheduledFuture<?> task;
    private final ScraperWrapper wrapper;

    public ScrapingTask(ScheduledFuture<?> task, ScraperWrapper wrapper) {
        this.task = task;
        this.wrapper = wrapper;
    }

    public void cancel(boolean mayInterruptIfRunning) {
        task.cancel(mayInterruptIfRunning);
        wrapper.closeResource();
    }

    public static class ScraperWrapper implements Runnable {

        private final WebScraper original;

        public ScraperWrapper(WebScraper original) {
            this.original = original;
        }

        public static ScraperWrapper of(WebDriver driver,
                                        ModelUrl modelUrl,
                                        ProductContainer productContainer) {
            return new ScraperWrapper(new WebScraper(driver, modelUrl, productContainer));
        }

        @Override
        public void run() {
            synchronized (this) {
                original.run();
            }
        }

        public void closeResource() {
            synchronized (this) {
                original.quitDriver();
            }
        }
    }
}
