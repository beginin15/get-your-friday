package com.toy.getyourfriday.web;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import com.toy.getyourfriday.dto.RegisterRequest;
import com.toy.getyourfriday.dto.RemoveRequest;
import com.toy.getyourfriday.dto.UpdateDTO;
import com.toy.getyourfriday.exception.UserNotFoundException;
import com.toy.getyourfriday.request.Command;
import com.toy.getyourfriday.response.ModelsResponse;
import com.toy.getyourfriday.response.UserResponse;
import com.toy.getyourfriday.response.WebClientResponse;
import com.toy.getyourfriday.service.BotMessageManager;
import com.toy.getyourfriday.service.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class WebhookController {

    private final ModelUrlParser modelUrlParser;
    private final ScrapingService scrapingService;
    private final BotMessageManager messageManager;

    @Autowired
    public WebhookController(ModelUrlParser modelUrlParser,
                             ScrapingService scrapingService,
                             BotMessageManager messageManager) {
        this.modelUrlParser = modelUrlParser;
        this.scrapingService = scrapingService;
        this.messageManager = messageManager;
    }

    @PostMapping("/${bot.token}")
    public void getMessage(@RequestBody UpdateDTO content) {
        // 리팩토링 필요
        WebClientResponse response = null;
        String errorMessage = null;

        try {
            String text = content.getMessage().getText();
            Command command = Command.from(text);
            switch (command) {
                case MODELS:
                    response = ModelsResponse.of(content, modelUrlParser.getAllModelNames());
                    break;
                case REMOVE:
                    response = scrapingService.remove(RemoveRequest.from(content));
                    break;
                case REGISTER:
                    ModelUrl modelUrl = Optional.ofNullable(modelUrlParser.findByName(command.extractCommand(text)))
                            .orElseThrow(() -> new IllegalArgumentException("모델명이 올바르지 않습니다. 다시 확인해주세요."));
                    response = scrapingService.register(RegisterRequest.of(content, modelUrl));
                    break;
            }
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            response = UserResponse.of(content, "사용자 정보가 잘못되었거나 알람 모델이 존재하지 않습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            response = UserResponse.of(content, e.getMessage());
        } catch (RuntimeException | IllegalAccessException e) {
            e.printStackTrace();
            response = UserResponse.of(content, "서버 오류");
        }
        messageManager.send(response);
    }
}
