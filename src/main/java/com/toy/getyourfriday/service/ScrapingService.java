package com.toy.getyourfriday.service;

import com.toy.getyourfriday.dto.RegisterRequest;
import com.toy.getyourfriday.dto.RemoveRequest;
import com.toy.getyourfriday.response.WebClientResponse;

public interface ScrapingService {

    WebClientResponse register(RegisterRequest registerRequest);
    WebClientResponse remove(RemoveRequest removeRequest);
}
