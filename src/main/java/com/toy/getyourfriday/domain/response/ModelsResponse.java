package com.toy.getyourfriday.domain.response;

import com.toy.getyourfriday.dto.InlineKeyboardMarkup;
import com.toy.getyourfriday.dto.UpdateDTO;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@Getter
public class ModelsResponse extends WebClientResponse {

    public static final String MESSAGE = "원하는 모델을 선택한 뒤 입력창에 자동 생성된 텍스트를 전송해주세요.";
    private final List<String> modelNames;

    public ModelsResponse(Integer chatId, List<String> modelNames) {
        super(chatId);
        this.modelNames = modelNames;
    }

    public static ModelsResponse of(Integer chatId, List<String> modelNames) {
        return new ModelsResponse(chatId, modelNames);
    }

    public static ModelsResponse of(UpdateDTO updateDTO, List<String> modelNames) {
        return new ModelsResponse(updateDTO.getMessage().getFrom().getChatId(), modelNames);
    }

    @Override
    public void send(WebClient webClient) {
        webClient.post()
                .uri(this::buildUri)
                .bodyValue(InlineKeyboardMarkup.from(modelNames))
                .retrieve()
                .bodyToMono(InlineKeyboardMarkup.class)
                .subscribe();
    }

    @Override
    protected URI buildUri(UriBuilder uriBuilder) {
        return uriBuilder.queryParam("chat_id", chatId)
                .queryParam("text", MESSAGE)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelsResponse)) return false;
        if (!super.equals(o)) return false;
        ModelsResponse that = (ModelsResponse) o;
        return getModelNames().equals(that.getModelNames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getModelNames());
    }
}
