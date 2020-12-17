package com.toy.getyourfriday.config;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
@EnableScheduling
public class ApplicationConfig {

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
        return new ConcurrentTaskScheduler();
    }
}
