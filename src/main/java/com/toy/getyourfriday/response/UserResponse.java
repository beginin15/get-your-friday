package com.toy.getyourfriday.response;

import com.toy.getyourfriday.domain.user.User;
import com.toy.getyourfriday.dto.InlineKeyboardMarkup;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

@Getter
public class UserResponse extends WebClientResponse {

    private final String message;

    public UserResponse(Integer chatId, String message) {
        super(chatId);
        this.message = message;
    }

    public static UserResponse of(User user, String message) {
        return new UserResponse(user.getChatId(), message);
    }

    @Override
    public Mono<?> send(WebClient webClient) {
        return webClient.get()
                .uri(this::buildUri)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientResponse::mapToHttpStatusCodeException)
                .bodyToMono(InlineKeyboardMarkup.class);
    }

    @Override
    protected URI buildUri(UriBuilder uriBuilder) {
        return uriBuilder.queryParam("chat_id", chatId)
                .queryParam("text", message)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserResponse)) return false;
        if (!super.equals(o)) return false;
        UserResponse that = (UserResponse) o;
        return getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMessage());
    }
}
