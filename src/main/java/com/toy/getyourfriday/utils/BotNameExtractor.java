package com.toy.getyourfriday.utils;

public class BotNameExtractor {

    private static final String BOT_NAME = "@get_your_friday_bot ";

    public static String extractBotName(String text) {
        return text.replace(BOT_NAME, "");
    }
}
