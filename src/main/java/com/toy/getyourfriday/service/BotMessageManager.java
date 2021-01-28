package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.response.WebClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service("messageManager")
public class BotMessageManager {

    private final WebClient webClient;

    @Autowired
    public BotMessageManager(WebClient webClient) {
        this.webClient = webClient;
    }

    public void send(WebClientResponse response) {
        response.send(webClient)
                .subscribe(System.out::println, System.out::println);
    }
}
