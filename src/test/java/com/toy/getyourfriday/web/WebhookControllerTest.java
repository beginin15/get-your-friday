package com.toy.getyourfriday.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toy.getyourfriday.dto.MessageDTO;
import com.toy.getyourfriday.dto.UpdateDTO;
import com.toy.getyourfriday.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class WebhookControllerTest {

    @Autowired
    WebApplicationContext wac;

    @Value("${bot.token}")
    private String token;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.mapper = new ObjectMapper();
    }

    @Test
    void getMessage() throws Exception {
        // given
        UpdateDTO content = UpdateDTO.builder()
                .updateId(1)
                .message(MessageDTO.builder()
                        .from(UserDTO.builder()
                                .chatId(1471869468)
                                .isBot(false)
                                .firstName("Jay")
                                .lastName("K")
                                .build())
                        .text("/models")
                        .build())
                .build();

        // when
        mockMvc.perform(post("/" + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(content)))
                .andExpect(status().isOk());
    }
}
