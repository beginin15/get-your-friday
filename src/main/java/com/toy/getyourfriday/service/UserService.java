package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.User;
import com.toy.getyourfriday.domain.UserRepository;
import com.toy.getyourfriday.dto.RemoveRequest;
import com.toy.getyourfriday.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ScrapingManager scrapingManager;

    @Autowired
    public UserService(UserRepository userRepository, ScrapingManager scrapingManager) {
        this.userRepository = userRepository;
        this.scrapingManager = scrapingManager;
    }

    public BotResponse register(User user) {
        userRepository.save(user);
        if (!scrapingManager.containsModelUrl(user.getMonitoredUrl())) {
            scrapingManager.register(user.getMonitoredUrl());
        }
        return BotResponse.REGISTER_SUCCESS;
    }

    public BotResponse remove(RemoveRequest removeRequest) {
        User user = userRepository.findById(removeRequest.getChatId())
                .orElseThrow(() -> new UserNotFoundException(removeRequest.getChatId()));
        userRepository.deleteById(user.getChatId());
        if (userRepository.countByMonitoredUrl(removeRequest.getModelUrl()) <= 0) {
            scrapingManager.remove(removeRequest.getModelUrl());
        }
        return BotResponse.REMOVE_SUCCESS;
    }
}
