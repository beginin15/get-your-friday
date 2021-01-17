package com.toy.getyourfriday.service;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.User;
import com.toy.getyourfriday.domain.UserRepository;
import com.toy.getyourfriday.dto.RegisterRequest;
import com.toy.getyourfriday.dto.RemoveRequest;
import com.toy.getyourfriday.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        SingleSelectionService.class,
        ModelUrlParser.class
})
class SingleSelectionServiceTest {

    @Autowired
    private ModelUrlParser modelUrlParser;

    @Autowired
    @Qualifier(value = "singleSelectionService")
    private ScrapingService scrapingService;

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
        RegisterRequest registerRequest = RegisterRequest.builder()
                .chatId(chatId)
                .modelUrl(modelUrl)
                .build();
        User user = User.from(registerRequest);

        when(userRepository.save(user))
                .thenReturn(user);
        when(scrapingManager.containsModelUrl(modelUrl))
                .thenReturn(false);
        doNothing().when(scrapingManager).registerIfNotExist(modelUrl);

        // when
        BotResponse response = scrapingService.register(registerRequest);

        // then
        verify(userRepository).save(user);
        verify(scrapingManager).registerIfNotExist(modelUrl);
        assertThat(response).isEqualTo(BotResponse.REGISTER_SUCCESS);
    }

    @Test
    @DisplayName("register 수행 시, DynamoDB Exception 발생")
    void registerWithException() {
        // given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .chatId(chatId)
                .modelUrl(modelUrl)
                .build();
        User user = User.from(registerRequest);

        when(userRepository.save(user))
                .thenThrow(new AmazonDynamoDBException("something"));

        // when, then
        assertThatThrownBy(() -> scrapingService.register(registerRequest))
                .isInstanceOf(AmazonDynamoDBException.class);
        verify(scrapingManager, never())
                .containsModelUrl(modelUrl);
        verify(scrapingManager, never())
                .registerIfNotExist(modelUrl);
    }

    @Test
    @DisplayName("User 제거 성공")
    void remove() {
        // given
        RemoveRequest removeRequest = RemoveRequest.builder()
                .chatId(chatId)
                .modelUrl(modelUrl)
                .build();

        stubToRemove(removeRequest);
        when(userRepository.countByMonitoredUrl(modelUrl))
                .thenReturn(0);

        // when
        BotResponse response = scrapingService.remove(removeRequest);

        // then
        InOrder inOrder = inOrder(userRepository, scrapingManager);
        inOrder.verify(userRepository)
                .findById(chatId);
        inOrder.verify(userRepository)
                .deleteById(chatId);
        inOrder.verify(userRepository)
                .countByMonitoredUrl(modelUrl);
        inOrder.verify(scrapingManager)
                .remove(modelUrl);
        assertThat(response).isEqualTo(BotResponse.REMOVE_SUCCESS);
    }

    @Test
    @DisplayName("User 제거 시, task는 유지")
    void removeWhenMonitoredUrlRemains() {
        // given
        RemoveRequest removeRequest = RemoveRequest.builder()
                .chatId(chatId)
                .modelUrl(modelUrl)
                .build();

        stubToRemove(removeRequest);
        when(userRepository.countByMonitoredUrl(modelUrl))
                .thenReturn(1);

        // when
        BotResponse response = scrapingService.remove(removeRequest);

        // then
        InOrder inOrder = inOrder(userRepository, scrapingManager);
        inOrder.verify(userRepository).findById(chatId);
        inOrder.verify(userRepository).deleteById(chatId);
        inOrder.verify(userRepository).countByMonitoredUrl(modelUrl);
        assertThat(response).isEqualTo(BotResponse.REMOVE_SUCCESS);
    }

    @Test
    @DisplayName("remove 수행 시, UserNotFoundException 발생")
    void removeWithException() {
        // given
        RemoveRequest removeRequest = RemoveRequest.builder()
                .chatId(chatId)
                .modelUrl(modelUrl)
                .build();

        when(userRepository.findById(chatId))
                .thenThrow(UserNotFoundException.class);

        // when, then
        assertThatThrownBy(() -> scrapingService.remove(removeRequest))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never())
                .deleteById(chatId);
        verify(userRepository, never())
                .countByMonitoredUrl(modelUrl);
        verify(scrapingManager, never())
                .remove(modelUrl);
    }

    private void stubToRemove(RemoveRequest removeRequest) {
        when(userRepository.findById(chatId))
                .thenReturn(Optional.of(new User(chatId, modelUrl)));
        doNothing().when(userRepository)
                .deleteById(chatId);
        when(userRepository.countByMonitoredUrl(modelUrl))
                .thenReturn(0);
        doNothing().when(scrapingManager)
                .remove(modelUrl);
    }
}
