package com.toy.getyourfriday.component;

import com.toy.getyourfriday.domain.ModelUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@PropertySource("classpath:models.properties")
public class ModelUrlParser {

    @Value("#{${model.map}}")
    private Map<String, ModelUrl> BY_NAME;

    public ModelUrl findByName(String name) {
        return BY_NAME.get(name);
    }
}
