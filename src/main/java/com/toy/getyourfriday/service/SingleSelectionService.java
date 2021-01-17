package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.ModelUrl;
import com.toy.getyourfriday.domain.User;
import com.toy.getyourfriday.domain.UserRepository;
import com.toy.getyourfriday.dto.RegisterRequest;
import com.toy.getyourfriday.dto.RemoveRequest;
import com.toy.getyourfriday.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SingleSelectionService implements ScrapingService {

    private final UserRepository userRepository;
    private final ScrapingManager scrapingManager;

    @Autowired
    public SingleSelectionService(UserRepository userRepository, ScrapingManager scrapingManager) {
        this.userRepository = userRepository;
        this.scrapingManager = scrapingManager;
    }

    @Override
    public BotResponse register(RegisterRequest registerRequest) {
        User user = userRepository.save(User.from(registerRequest));
        scrapingManager.registerIfNotExist(user.getMonitoredUrl());
        return BotResponse.REGISTER_SUCCESS;
    }

    @Override
    public BotResponse remove(RemoveRequest removeRequest) {
        User user = userRepository.findById(removeRequest.getChatId())
                .orElseThrow(() -> new UserNotFoundException(removeRequest.getChatId()));
        userRepository.deleteById(user.getChatId());
        removeTaskIfNotMonitored(user.getMonitoredUrl());
        return BotResponse.REMOVE_SUCCESS;
    }

    private void removeTaskIfNotMonitored(ModelUrl modelUrl) {
        if (userRepository.countByMonitoredUrl(modelUrl) <= 0) {
            scrapingManager.remove(modelUrl);
        }
    }
}
