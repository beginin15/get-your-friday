package com.toy.getyourfriday.dto;

import com.toy.getyourfriday.domain.ModelUrl;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RemoveRequest {

    private final Integer chatId;
    private final ModelUrl modelUrl;

    public static RemoveRequest from(UpdateDTO updateDTO, ModelUrl modelUrl) {
        return RemoveRequest.builder()
                .chatId(updateDTO.getMessage().getFrom().getChatId())
                .modelUrl(modelUrl)
                .build();
    }
}
