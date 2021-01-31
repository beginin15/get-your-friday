package com.toy.getyourfriday.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.user.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {
        ModelUrlParser.class
})
class UserResponseTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private ModelUrlParser modelUrlParser;

    private WebClient webClient;
    private ObjectMapper objectMapper;
    private User user;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void setUp() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:" + mockWebServer.getPort())
                .build();
        this.objectMapper = new ObjectMapper();
        this.user = new User(0001, modelUrlParser.findByName("lassie"));
    }

    @Test
    @DisplayName("정적 팩토리 메소드")
    void of() {
        UserResponse actual = UserResponse.of(user, "메세지");
        assertThat(actual).isEqualTo(new UserResponse(0001, "메세지"));
        assertThat(actual).isNotEqualTo(new UserResponse(0002, "메세지"));
    }

    @Test
    @DisplayName("사용자(모델) 등록 및 제거 메세지 전송")
    void send() throws InterruptedException, UnsupportedEncodingException {
        // mocking
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // given
        String message = "등록 완료";
        UserResponse response = UserResponse.of(user, message);

        // when
        response.send(webClient).block();

        // then
        RecordedRequest request = mockWebServer.takeRequest();
        String path = String.format("/?chat_id=%d&text=%s", user.getChatId(), message);

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(URLDecoder.decode(request.getPath(), "UTF-8")).isEqualTo(path);
    }

    @ParameterizedTest()
    @ValueSource(ints = {400, 500})
    @DisplayName("4XX or 5XX 예러")
    void sendWithHttpStatusCodeException(int statusCode) {
        // mocking
        mockWebServer.enqueue(new MockResponse().setResponseCode(statusCode));

        // given
        String message = "등록 완료";
        UserResponse response = UserResponse.of(user, message);

        // when
        Mono<?> result = response.send(webClient);

        // then
        assertThatThrownBy(result::block).isInstanceOf(HttpStatusCodeException.class);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }
}
