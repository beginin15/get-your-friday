package com.toy.getyourfriday.component;

import com.google.common.collect.ImmutableList;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Component
@PropertySource("classpath:models.properties")
public class ModelUrlParser {

    @Value("#{${model.map}}")
    private Map<String, ModelUrl> BY_NAME;

    public ModelUrl findByName(String name) {
        return BY_NAME.get(name.toLowerCase());
    }

    public List<String> getAllModelNames() {
        return BY_NAME.keySet()
                .stream()
                .collect(collectingAndThen(toList(), ImmutableList::copyOf));
    }
}
