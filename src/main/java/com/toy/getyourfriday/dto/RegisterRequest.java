package com.toy.getyourfriday.dto;

import com.toy.getyourfriday.domain.scraping.ModelUrl;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterRequest {

    private final Integer chatId;
    private final ModelUrl modelUrl;

    public static RegisterRequest of(UpdateDTO update, ModelUrl modelUrl) {
        return RegisterRequest.builder()
                .chatId(update.getMessage().getFrom().getChatId())
                .modelUrl(modelUrl)
                .build();
    }
}
