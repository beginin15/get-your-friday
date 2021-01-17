package com.toy.getyourfriday.service;

import com.toy.getyourfriday.dto.RegisterRequest;
import com.toy.getyourfriday.dto.RemoveRequest;

public interface ScrapingService {

    BotResponse register(RegisterRequest registerRequest);
    BotResponse remove(RemoveRequest removeRequest);
}
