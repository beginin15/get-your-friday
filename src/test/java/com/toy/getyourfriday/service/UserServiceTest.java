package com.toy.getyourfriday.service;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.User;
import com.toy.getyourfriday.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {
        UserService.class,
        ModelUrlParser.class
})
class UserServiceTest {

    @Autowired
    private ModelUrlParser modelUrlParser;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ScrapingManager scrapingManager;

    private Integer chatId;
    private ModelUrl modelUrl;

    @BeforeEach
    void setUp() {
        this.chatId = 1234;
        this.modelUrl = modelUrlParser.findByName("lassie");
    }

    @Test
    @DisplayName("User 및 ModelUrl 등록")
    void register() {
        // given
        User user = new User(chatId, modelUrl);

        when(userRepository.save(user))
                .thenReturn(user);
        when(scrapingManager.containsModelUrl(modelUrl))
                .thenReturn(false);
        when(scrapingManager.register(modelUrl))
                .thenReturn(true);

        // when
        BotResponse response = userService.register(user);

        // then
        verify(userRepository).save(user);
        verify(scrapingManager).containsModelUrl(modelUrl);
        verify(scrapingManager).register(modelUrl);
        assertThat(response).isEqualTo(BotResponse.REGISTER_SUCCESS);
    }

    @Test
    @DisplayName("DynamoDB Exception 발생")
    void register_exception() {
        // given
        User user = new User(chatId, modelUrl);

        when(userRepository.save(user))
                .thenThrow(new AmazonDynamoDBException("something"));

        // when, then
        assertThatThrownBy(() -> userService.register(user))
                .isInstanceOf(AmazonDynamoDBException.class);
        verify(scrapingManager, never())
                .containsModelUrl(modelUrl);
        verify(scrapingManager, never())
                .register(modelUrl);
    }
}
