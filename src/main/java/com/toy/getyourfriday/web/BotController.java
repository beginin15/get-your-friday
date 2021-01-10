package com.toy.getyourfriday.web;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.User;
import com.toy.getyourfriday.dto.UpdateDTO;
import com.toy.getyourfriday.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final UserService userService;
    private final ModelUrlParser modelUrlParser;

    @Autowired
    public BotController(UserService userService, ModelUrlParser modelUrlParser) {
        this.userService = userService;
        this.modelUrlParser = modelUrlParser;
    }

    @PostMapping("/${bot.token}")
    public void getUpdate(@RequestBody UpdateDTO content) {
        // 리팩토링 필요
        ModelUrl modelUrl = modelUrlParser.findByName(content.getMessage().getText());
        if (modelUrl != null) {
            userService.register(new User(
                    content.getMessage().getFrom().getChatId(),
                    modelUrlParser.findByName(content.getMessage().getText()))
            );
        }

        // test webhook
        String uri = String.format("/bot%s/sendMessage", token);
        WebClient client = WebClient.create("https://api.telegram.org");
        Mono<String> result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParam("chat_id", content.getMessage().getFrom().getChatId())
                        .queryParam("text", "welcome!")
                        .build())
                .retrieve()
                .bodyToMono(String.class);
        result.subscribe(System.out::println);
    }
}
