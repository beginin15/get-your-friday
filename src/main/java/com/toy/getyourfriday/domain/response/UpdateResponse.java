package com.toy.getyourfriday.domain.response;

import com.toy.getyourfriday.domain.product.Product;
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
public class UpdateResponse extends WebClientResponse {

    private final Product updateProduct;

    public UpdateResponse(Integer chatId, Product updateProduct) {
        super(chatId);
        this.updateProduct = updateProduct;
    }

    public static UpdateResponse of(User user, Product updateProduct) {
        return new UpdateResponse(user.getChatId(), updateProduct);
    }

    @Override
    public Mono<?> send(WebClient webClient) {
        return webClient.post()
                .uri(this::buildUri)
                .bodyValue(InlineKeyboardMarkup.from(updateProduct))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientResponse::mapToHttpStatusCodeException)
                .bodyToMono(InlineKeyboardMarkup.class);
    }

    @Override
    protected URI buildUri(UriBuilder uriBuilder) {
        return uriBuilder.queryParam("chat_id", chatId)
                .queryParam("text", updateProduct.getThumbnail())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateResponse)) return false;
        if (!super.equals(o)) return false;
        UpdateResponse that = (UpdateResponse) o;
        return getUpdateProduct().equals(that.getUpdateProduct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUpdateProduct());
    }
}
