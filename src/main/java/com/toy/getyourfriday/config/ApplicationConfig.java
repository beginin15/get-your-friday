package com.toy.getyourfriday.config;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Value("${thread.times}")
    private int threadTimes;

    @Value("${bot.token}")
    private String botToken;

    @Bean
    public ChromeOptions chromeOptions() {
        // 프록시 설정
        Proxy proxy = new Proxy();
        proxy.setSslProxy("socks5://127.0.0.1:9050");

        ChromeOptions options = new ChromeOptions();
        options.setCapability("proxy", proxy);
        options.setHeadless(true);

        return options;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors() * threadTimes);
        return threadPoolTaskScheduler;
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(String.format("https://api.telegram.org/bot%s/sendMessage", botToken)).build();
    }
}
