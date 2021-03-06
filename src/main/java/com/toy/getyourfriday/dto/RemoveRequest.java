package com.toy.getyourfriday.dto;

import com.toy.getyourfriday.domain.scraping.ModelUrl;
import com.toy.getyourfriday.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RemoveRequest {

    private final Integer chatId;
    private final ModelUrl modelUrl;

    public static RemoveRequest of(UpdateDTO updateDTO, ModelUrl modelUrl) {
        return RemoveRequest.builder()
                .chatId(updateDTO.getMessage().getFrom().getChatId())
                .modelUrl(modelUrl)
                .build();
    }

    public static RemoveRequest from(UpdateDTO updateDTO) {
        return RemoveRequest.builder()
                .chatId(updateDTO.getMessage().getFrom().getChatId())
                .build();
    }

    public User toUser() {
        return new User(chatId, modelUrl);
    }
}
