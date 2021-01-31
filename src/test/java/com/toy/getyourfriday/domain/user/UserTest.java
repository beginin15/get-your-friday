package com.toy.getyourfriday.domain.user;

import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import com.toy.getyourfriday.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        ModelUrlParser.class
})
class UserTest {

    @Autowired
    private ModelUrlParser modelUrlParser;

    @Test
    @DisplayName("ModelUrl 변경")
    void changeMonitoredUrl() {
        // given
        User actual = new User(1234, modelUrlParser.findByName("lassie"));
        ModelUrl modified = modelUrlParser.findByName("topcat");

        // when
        actual = actual.changeMonitoredUrl(modified);

        // then
        assertThat(actual).isEqualTo(new User(1234, modified));
    }

    @Test
    @DisplayName("정적 팩토리 메소드")
    void from() {
        // given
        ModelUrl modelUrl = modelUrlParser.findByName("lassie");
        RegisterRequest request = RegisterRequest.of(1234, modelUrl);

        // when
        User actual = User.from(request);

        // then
        assertThat(actual).isEqualTo(new User(1234, modelUrl));
    }
}
