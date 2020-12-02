package com.toy.getyourfriday.web;

import com.toy.getyourfriday.dto.UpdateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class BotController {

    @Value("${bot.token}")
    private String token;

    @PostMapping("/${bot.token}")
    public void getUpdate(@RequestBody UpdateDTO content) {
        // test webhook
        String uri = String.format("/bot%s/sendMessage", token);
        WebClient client = WebClient.create("https://api.telegram.org");
        Mono<String> result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParam("chat_id", content.getMessage().getFrom().getId())
                        .queryParam("text", "welcome!")
                        .build())
                .retrieve()
                .bodyToMono(String.class);
        result.subscribe(System.out::println);
    }
}
