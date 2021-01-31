package com.toy.getyourfriday.service;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.product.Product;
import com.toy.getyourfriday.domain.product.Products;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import com.toy.getyourfriday.domain.user.User;
import com.toy.getyourfriday.domain.user.UserRepository;
import com.toy.getyourfriday.response.UpdateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        ModelUrlParser.class,
        UpdateService.class
})
class UpdateServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BotMessageManager messageManager;

    @Autowired
    private ModelUrlParser modelUrlParser;

    @Autowired
    private UpdateService updateService;

    private ModelUrl modelUrl;
    private Products updatedProducts;

    @BeforeEach
    void setUp() {
        this.modelUrl = modelUrlParser.findByName("lassie");
        this.updatedProducts = Products.of(
                new Product("https://www.freitag.ch/ko/f11?productID=1143270",
                        "https://freitag.rokka.io/neo_square_thumbnail/abc/0000-1.jpg"),
                new Product("https://www.freitag.ch/ko/f11?productID=1153144",
                        "https://freitag.rokka.io/neo_square_thumbnail/abc/0000-2.jpg"),
                new Product("https://www.freitag.ch/ko/f11?productID=1154962",
                        "https://freitag.rokka.io/neo_square_thumbnail/abc/0000-3.jpg")
        );
    }

    @Test
    @DisplayName("업데이트 정보 생성 및 전달")
    void update() {
        // given
        List<User> users = createUserList(
                new User(0001, modelUrl),
                new User(0002, modelUrl),
                new User(0003, modelUrl)
        );
        List<List<UpdateResponse>> responses = users.stream()
                .map(updatedProducts::responses)
                .collect(Collectors.toList());

        when(userRepository.findByMonitoredUrl(modelUrl))
                .thenReturn(users);
        responses.forEach(this::mockingUpdateResponseList);

        // when
        updateService.update(modelUrl, updatedProducts);

        // then
        verify(userRepository).findByMonitoredUrl(modelUrl);
        responses.forEach(this::verifyUpdateResponseList);
    }

    private void mockingUpdateResponseList(List<UpdateResponse> responses) {
        responses.forEach(r -> doNothing().when(messageManager).send(r));
    }

    private void verifyUpdateResponseList(List<UpdateResponse> responses) {
        responses.forEach(r -> verify(messageManager).send(r));
    }

    public static List<User> createUserList(User...users) {
        return Arrays.stream(users)
                .collect(Collectors.toList());
    }
}
