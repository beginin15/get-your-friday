package com.toy.getyourfriday.dto;

import com.toy.getyourfriday.domain.scraping.ModelUrl;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterRequest {

    private final Integer chatId;
    private final ModelUrl modelUrl;

    public static RegisterRequest of(UpdateDTO updateDTO, ModelUrl modelUrl) {
        return RegisterRequest.builder()
                .chatId(updateDTO.getMessage().getFrom().getChatId())
                .modelUrl(modelUrl)
                .build();
    }

    public static RegisterRequest of(Integer chatId, ModelUrl modelUrl) {
        return RegisterRequest.builder()
                .chatId(chatId)
                .modelUrl(modelUrl)
                .build();
    }
}
