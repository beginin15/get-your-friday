package com.toy.getyourfriday.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@AllArgsConstructor
@EqualsAndHashCode
public abstract class WebClientResponse {

    protected Integer chatId;

    protected static Mono<? extends Throwable> mapToHttpStatusCodeException(ClientResponse response) {
        HttpStatus statusCode = response.statusCode();
        if (statusCode.is4xxClientError()) {
            return Mono.error(new HttpClientErrorException(statusCode));
        }
        if (statusCode.is5xxServerError()) {
            return Mono.error(new HttpServerErrorException(statusCode));
        }
        return response.createException();
    }

    public abstract Mono<?> send(WebClient webClient);

    protected abstract URI buildUri(UriBuilder uriBuilder);
}
