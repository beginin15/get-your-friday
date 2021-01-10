package com.toy.getyourfriday.service;

public enum BotResponse {
    REGISTER_SUCCESS("등록 성공");

    private final String message;

    BotResponse(String message) {
        this.message = message;
    }
}
