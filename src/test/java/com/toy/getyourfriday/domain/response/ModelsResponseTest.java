package com.toy.getyourfriday.domain.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.user.User;
import com.toy.getyourfriday.dto.InlineKeyboardMarkup;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        ModelUrlParser.class
})
class ModelsResponseTest {

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
        ModelsResponse actual = ModelsResponse.of(1234, Arrays.asList("test1", "test2"));
        assertThat(actual).isEqualTo(new ModelsResponse(1234, Arrays.asList("test1", "test2")));
    }

    @Test
    @DisplayName("전체 모델 메세지 전송")
    void send() throws InterruptedException, JsonProcessingException, UnsupportedEncodingException {
        // given
        List<String> modelNames = modelUrlParser.getAllModelNames();

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // when
        messageManager.send(ModelsResponse.of(user.getChatId(), modelNames));

        // then
        RecordedRequest request = mockWebServer.takeRequest();
        String path = String.format("/?chat_id=%d&text=%s", user.getChatId(), ModelsResponse.MESSAGE);
        String body = objectMapper.writeValueAsString(InlineKeyboardMarkup.from(modelNames));

        System.out.println(body);

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(URLDecoder.decode(request.getPath(), "UTF-8")).isEqualTo(path);
        assertThat(request.getBody().readUtf8()).isEqualTo(body);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }
}
