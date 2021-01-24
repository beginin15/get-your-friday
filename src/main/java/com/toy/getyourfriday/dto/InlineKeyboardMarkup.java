package com.toy.getyourfriday.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.toy.getyourfriday.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.toy.getyourfriday.dto.InlineKeyboardMarkup.InlineKeyboards.PurchaseLinkButton;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@AllArgsConstructor
public class InlineKeyboardMarkup {

    @JsonProperty("reply_markup")
    private InlineKeyboards buttons;

    public static InlineKeyboardMarkup from(Product product) {
        List<List<PurchaseLinkButton>> inlineKeyboards = Arrays.asList(product)
                .stream()
                .map(p -> new PurchaseLinkButton("GET", p.getLink(), null))
                .collect(collectingAndThen(toList(), ImmutableList::of));
        return fromButtonList(inlineKeyboards);
    }

    public static InlineKeyboardMarkup from(List<String> modelNames) {
        List<List<PurchaseLinkButton>> inlineKeyboards = modelNames.stream()
                .map(m -> new PurchaseLinkButton(m, null, "/" + m))
                .collect(collectingAndThen(toList(), ImmutableList::of));
        return fromButtonList(inlineKeyboards);
    }

    private static InlineKeyboardMarkup fromButtonList(List<List<PurchaseLinkButton>> inlineKeyboards) {
        return new InlineKeyboardMarkup(new InlineKeyboards(inlineKeyboards));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class InlineKeyboards {

        @JsonProperty("inline_keyboard")
        private List<List<PurchaseLinkButton>> inlineKeyboards;

        @NoArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class PurchaseLinkButton {

            @JsonProperty
            private String text;
            @JsonProperty
            private String url;
            @JsonProperty("switch_inline_query")
            private String inlineQuery;

            public PurchaseLinkButton(String text, String url, String inlineQuery) {
                this.text = text;
                this.url = url;
                this.inlineQuery = inlineQuery;
            }
        }
    }
}
