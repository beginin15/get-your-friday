package com.toy.getyourfriday.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.toy.getyourfriday.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.toy.getyourfriday.dto.InlineKeyboardMarkup.InlineKeyboards.InlineButton;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@AllArgsConstructor
public class InlineKeyboardMarkup {

    @JsonProperty("reply_markup")
    private InlineKeyboards buttons;

    public static InlineKeyboardMarkup from(Product product) {
        List<List<InlineButton>> buttons = Arrays.asList(product)
                .stream()
                .map(p -> new InlineButton("GET", p.getLink(), null))
                .collect(collectingAndThen(toList(), ImmutableList::of));
        return fromButtonList(buttons);
    }

    public static InlineKeyboardMarkup from(List<String> modelNames) {
        List<List<InlineButton>> buttons = modelNames.stream()
                .map(m -> Collections.singletonList(new InlineButton(m, null, "/register " + m)))
                .collect(toList());
        return fromButtonList(buttons);
    }

    private static InlineKeyboardMarkup fromButtonList(List<List<InlineButton>> inlineKeyboards) {
        return new InlineKeyboardMarkup(new InlineKeyboards(inlineKeyboards));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class InlineKeyboards {

        @JsonProperty("inline_keyboard")
        private List<List<InlineButton>> inlineKeyboards;

        @NoArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class InlineButton {

            @JsonProperty
            private String text;
            @JsonProperty
            private String url;
            @JsonProperty("switch_inline_query_current_chat")
            private String inlineQuery;

            public InlineButton(String text, String url, String inlineQuery) {
                this.text = text;
                this.url = url;
                this.inlineQuery = inlineQuery;
            }
        }
    }
}
