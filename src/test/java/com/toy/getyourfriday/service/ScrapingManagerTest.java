package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.component.ProductContainer;
import com.toy.getyourfriday.config.ApplicationConfig;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        ProductContainer.class,
        ModelUrlParser.class
})
@SpringJUnitConfig(classes = ApplicationConfig.class)
@Execution(ExecutionMode.CONCURRENT)
class ScrapingManagerTest {

    public static final String MODEL_NAME = "lassie";

    @MockBean
    private UpdateService updateService;

    @Autowired
    private TaskScheduler taskScheduler;
    @Autowired
    private ProductContainer productContainer;
    @Autowired
    private ChromeOptions chromeOptions;
    @Autowired
    private ModelUrlParser parser;

    private ScrapingManager actual;

    @BeforeEach
    void setUp() {
        actual = new ScrapingManager(taskScheduler, productContainer, chromeOptions);
    }

    @AfterEach
    void after() {
        actual.destroy();
    }

    @Test
    @DisplayName("스크래퍼 등록")
    void register() throws InterruptedException {
        // when
        actual.registerIfNotExist(parser.findByName(MODEL_NAME));

        Thread.sleep(100_000);

        // then
        ProductContainer unexpected = new ProductContainer(updateService);
        assertThat(productContainer).isNotEqualTo(unexpected);
    }

    @Test
    @DisplayName("스크래퍼 여러 개 등록")
    void registerSeveralTasks() throws InterruptedException {
        // given
        ModelUrl lassieUrl = parser.findByName("lassie");
        ModelUrl furyUrl = parser.findByName("fury");

        // when
        new Thread(() -> actual.registerIfNotExist(lassieUrl)).start();
        new Thread(() -> actual.registerIfNotExist(furyUrl)).start();

        Thread.sleep(100_000);

        // then
        assertThat(actual.containsModelUrl(lassieUrl)).isTrue();
        assertThat(actual.containsModelUrl(furyUrl)).isTrue();
    }

    @Test
    @DisplayName("멀티 스레드에서 동일한 스크래퍼 등록 시도")
    void registerByMultipleThreads() throws InterruptedException {
        // given
        ModelUrl modelUrl = parser.findByName("lassie");
        int count = 4;

        // when
        IntStream.range(0, count)
                .mapToObj(i -> (Runnable) () -> actual.registerIfNotExist(modelUrl))
                .map(Thread::new)
                .forEach(Thread::start);

        ScrapingManager expected = new ScrapingManager(taskScheduler, productContainer, chromeOptions);
        expected.registerIfNotExist(modelUrl);
        Thread.sleep(30_000);

        // then
        assertThat(actual).isEqualTo(expected);
        closeResource(expected);
    }

    @Test
    @DisplayName("스크래퍼 제거")
    void remove() throws InterruptedException {
        // given
        ModelUrl modelUrl = parser.findByName(MODEL_NAME);
        actual.registerIfNotExist(modelUrl);

        Thread.sleep(100_000);

        // when
        actual.remove(modelUrl);

        // then
        ScrapingManager expected = new ScrapingManager(taskScheduler, productContainer, chromeOptions);
        assertThat(actual).isEqualTo(expected);
        assertThat(actual.containsModelUrl(modelUrl)).isFalse();
    }

    private void closeResource(ScrapingManager... managers) {
        Arrays.stream(managers).forEach(ScrapingManager::destroy);
    }
}
