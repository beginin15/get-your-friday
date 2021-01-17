package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.config.ApplicationConfig;
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
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        ProductContainer.class,
        ModelUrlParser.class
})
@SpringJUnitConfig(classes = ApplicationConfig.class)
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
        scrapingManager.registerIfNotExist(parser.findByName(MODEL_NAME));

        Thread.sleep(100_000);

        // then
        ProductContainer unexpected = new ProductContainer();
        assertThat(productContainer).isNotEqualTo(unexpected);
    }

    @Test
    @DisplayName("스크래퍼 여러 개 등록")
    void registerSeveralTasks() throws InterruptedException {
        // given
        ModelUrl lassieUrl = parser.findByName("lassie");
        ModelUrl furyUrl = parser.findByName("fury");

        // when
        new Thread(() -> scrapingManager.registerIfNotExist(lassieUrl)).start();
        new Thread(() -> scrapingManager.registerIfNotExist(furyUrl)).start();

        Thread.sleep(100_000);

        // then
        assertThat(scrapingManager.containsModelUrl(lassieUrl)).isTrue();
        assertThat(scrapingManager.containsModelUrl(furyUrl)).isTrue();
    }

    @Test
    @DisplayName("스크래퍼 제거")
    void remove() throws InterruptedException {
        // given
        ModelUrl modelUrl = parser.findByName(MODEL_NAME);
        scrapingManager.registerIfNotExist(modelUrl);

        Thread.sleep(100_000);

        // when
        scrapingManager.remove(modelUrl);

        // then
        ScrapingManager expected = new ScrapingManager(taskScheduler, productContainer, chromeOptions);
        assertThat(scrapingManager).isEqualTo(expected);
        assertThat(scrapingManager.containsModelUrl(modelUrl)).isFalse();
    }
}
