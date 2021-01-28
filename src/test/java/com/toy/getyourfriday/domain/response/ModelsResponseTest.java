package com.toy.getyourfriday.domain.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.user.User;
import com.toy.getyourfriday.dto.InlineKeyboardMarkup;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {
        ModelUrlParser.class
})
class ModelsResponseTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private ModelUrlParser modelUrlParser;

    private WebClient webClient;
    private ObjectMapper objectMapper;
    private User user;
    private List<String> modelNames;

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
        this.modelNames = modelUrlParser.getAllModelNames();
    }

    @Test
    @DisplayName("정적 팩토리 메소드")
    void of() {
        ModelsResponse actual = ModelsResponse.of(1234, Arrays.asList("test1", "test2"));
        assertThat(actual).isEqualTo(new ModelsResponse(1234, Arrays.asList("test1", "test2")));
    }

    @Test
    @DisplayName("요청 메세지 형식")
    void send() throws InterruptedException, JsonProcessingException, UnsupportedEncodingException {
        // mocking
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // given
        ModelsResponse response = ModelsResponse.of(user.getChatId(), modelNames);

        // when
        response.send(webClient).block();

        // then
        RecordedRequest request = mockWebServer.takeRequest();
        String body = objectMapper.writeValueAsString(InlineKeyboardMarkup.from(modelNames));
        String path = String.format("/?chat_id=%d&text=%s", user.getChatId(), ModelsResponse.MESSAGE);

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(URLDecoder.decode(request.getPath(), "UTF-8")).isEqualTo(path);
        assertThat(request.getBody().readUtf8()).isEqualTo(body);
    }

    @ParameterizedTest()
    @ValueSource(ints = {400, 500})
    @DisplayName("4XX or 5XX 예러")
    void sendWithHttpStatusCodeException(int statusCode) {
        // mocking
        mockWebServer.enqueue(new MockResponse().setResponseCode(statusCode));

        // given
        ModelsResponse response = ModelsResponse.of(user.getChatId(), modelNames);

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
