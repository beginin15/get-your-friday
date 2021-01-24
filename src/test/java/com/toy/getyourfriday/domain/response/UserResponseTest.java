package com.toy.getyourfriday.domain.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.user.User;
import com.toy.getyourfriday.service.BotMessageManager;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        ModelUrlParser.class
})
class UserResponseTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private ModelUrlParser modelUrlParser;

    private WebClient webClient;
    private BotMessageManager messageManager;
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
        this.messageManager = new BotMessageManager(webClient);
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
        // given
        String message = "등록 완료";
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // when
        messageManager.send(UserResponse.of(user, message));

        // then
        RecordedRequest request = mockWebServer.takeRequest();
        String path = String.format("/?chat_id=%d&text=%s", user.getChatId(), message);

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(URLDecoder.decode(request.getPath(), "UTF-8")).isEqualTo(path);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }
}
