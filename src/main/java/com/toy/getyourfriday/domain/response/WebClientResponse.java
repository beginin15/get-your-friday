package com.toy.getyourfriday.domain.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;

@AllArgsConstructor
@EqualsAndHashCode
public abstract class WebClientResponse {

    protected Integer chatId;

    public abstract void send(WebClient webClient);

    protected abstract URI buildUri(UriBuilder uriBuilder);
}
