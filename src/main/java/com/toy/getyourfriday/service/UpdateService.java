package com.toy.getyourfriday.service;

import com.toy.getyourfriday.domain.product.Products;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import com.toy.getyourfriday.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {

    private final UserRepository userRepository;
    private final BotMessageManager messageManager;

    @Autowired
    public UpdateService(UserRepository userRepository, BotMessageManager messageManager) {
        this.userRepository = userRepository;
        this.messageManager = messageManager;
    }

    public void update(ModelUrl modelUrl, Products updatedProducts) {
        userRepository.findByMonitoredUrl(modelUrl)
                .stream()
                .map(updatedProducts::responses)
                .forEach(responses -> responses.forEach(messageManager::send));
    }
}
