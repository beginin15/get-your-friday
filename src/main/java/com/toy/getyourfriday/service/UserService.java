package com.toy.getyourfriday.service;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.toy.getyourfriday.domain.User;
import com.toy.getyourfriday.domain.UserRepository;
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

    public BotResponse register(User user) throws AmazonDynamoDBException {
        userRepository.save(user);
        if (!scrapingManager.containsModelUrl(user.getMonitoredUrl())) {
            scrapingManager.register(user.getMonitoredUrl());
        }
        return BotResponse.REGISTER_SUCCESS;
    }
}
