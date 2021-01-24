package com.toy.getyourfriday.component;

import com.toy.getyourfriday.domain.scraping.ModelUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ModelUrlParser.class)
class ModelUrlParserTest {

    @Autowired
    private ModelUrlParser parser;

    @Test
    @DisplayName("객체 생성")
    void create() {
        assertNotNull(parser);
    }

    @Test
    @DisplayName("이름에 따른 url 반환")
    void findByName() {
        assertEquals(new ModelUrl("https://www.freitag.ch/en/f41?items=showall"), parser.findByName("hawaiifive-o"));
    }
}
