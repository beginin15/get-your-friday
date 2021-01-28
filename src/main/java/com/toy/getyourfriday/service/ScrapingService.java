package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.response.WebClientResponse;
import com.toy.getyourfriday.dto.RegisterRequest;
import com.toy.getyourfriday.dto.RemoveRequest;

public interface ScrapingService {

    WebClientResponse register(RegisterRequest registerRequest);
    WebClientResponse remove(RemoveRequest removeRequest);
}
