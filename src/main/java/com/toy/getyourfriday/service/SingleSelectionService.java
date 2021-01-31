package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.scraping.ModelUrl;
import com.toy.getyourfriday.domain.user.User;
import com.toy.getyourfriday.domain.user.UserRepository;
import com.toy.getyourfriday.dto.RegisterRequest;
import com.toy.getyourfriday.dto.RemoveRequest;
import com.toy.getyourfriday.exception.UserNotFoundException;
import com.toy.getyourfriday.response.UserResponse;
import com.toy.getyourfriday.response.WebClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service @Primary
public class SingleSelectionService implements ScrapingService {

    private final UserRepository userRepository;
    private final ScrapingManager scrapingManager;

    @Autowired
    public SingleSelectionService(UserRepository userRepository, ScrapingManager scrapingManager) {
        this.userRepository = userRepository;
        this.scrapingManager = scrapingManager;
    }

    @Override
    public WebClientResponse register(RegisterRequest registerRequest) {
        User user = userRepository.save(User.from(registerRequest));
        scrapingManager.registerIfNotExist(user.getMonitoredUrl());
        return UserResponse.of(user, "등록 완료");
    }

    @Override
    public WebClientResponse remove(RemoveRequest removeRequest) {
        User user = userRepository.findById(removeRequest.getChatId())
                .orElseThrow(() -> new UserNotFoundException(removeRequest.getChatId()));
        userRepository.deleteById(user.getChatId());
        removeTaskIfNotMonitored(user.getMonitoredUrl());
        return UserResponse.of(user, "제거 완료");
    }

    private void removeTaskIfNotMonitored(ModelUrl modelUrl) {
        if (userRepository.countByMonitoredUrl(modelUrl) <= 0) {
            scrapingManager.remove(modelUrl);
        }
    }
}
