package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.domain.ModelUrl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TaskScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
class ScrapingManagerTest {

    public static final String MODEL_NAME = "lassie";

    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private ProductContainer productContainer;
    @Autowired
    private ChromeOptions chromeOptions;
    @Autowired
    private ModelUrlParser parser;

    private ScrapingManager scrapingManager;

    @BeforeEach
    void setUp() {
        scrapingManager = new ScrapingManager(taskScheduler, productContainer, chromeOptions);
    }

    @AfterEach
    void after() {
        scrapingManager.destroy();
    }

    @Test
    @DisplayName("스크래퍼 등록")
    void register() throws InterruptedException {
        // when
        scrapingManager.register(parser.findByName(MODEL_NAME));

        Thread.sleep(100_000);

        // then
        ProductContainer unexpected = new ProductContainer();
        assertNotEquals(unexpected, productContainer);
    }

    @Test
    @DisplayName("스크래퍼 여러 개 등록")
    void registerSeveralTasks() throws InterruptedException {
        // given
        ModelUrl lassieUrl = parser.findByName("lassie");
        ModelUrl furyUrl = parser.findByName("fury");

        // when
        new Thread(() -> scrapingManager.register(lassieUrl)).start();
        new Thread(() -> scrapingManager.register(furyUrl)).start();

        Thread.sleep(100_000);

        // then
        assertTrue(scrapingManager.containsModelUrl(lassieUrl));
        assertTrue(scrapingManager.containsModelUrl(furyUrl));
    }

    @Test
    @DisplayName("멀티 스레드 환경에서 동일한 스크래퍼 등록 시도")
    void registerInMultipleThreads() throws InterruptedException {
        List<Boolean> results = new ArrayList<>();

        // when
        int count = 4;
        IntStream.range(0, count)
                .mapToObj(i -> (Runnable) () -> {
                    results.add(scrapingManager.register(parser.findByName(MODEL_NAME)));
                })
                .map(Thread::new)
                .forEach(Thread::start);

        Thread.sleep(30_000);

        // then
        assertThat(results).containsExactlyInAnyOrder(true, false, false, false);
    }

    @Test
    @DisplayName("스크래퍼 제거")
    void remove() throws InterruptedException {
        // given
        ModelUrl modelUrl = parser.findByName(MODEL_NAME);
        scrapingManager.register(modelUrl);

        Thread.sleep(100_000);

        // when
        scrapingManager.remove(modelUrl);

        // then
        ScrapingManager expected = new ScrapingManager(taskScheduler, productContainer, chromeOptions);
        assertEquals(expected, scrapingManager);
        assertFalse(scrapingManager.containsModelUrl(modelUrl));
    }
}
