package com.toy.getyourfriday.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.toy.getyourfriday.domain.ModelUrl;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfig {

    public static class ModelUrlConverter implements DynamoDBTypeConverter<String, ModelUrl> {

        @Override
        public String convert(ModelUrl source) {
            return source.getUrl();
        }

        @Override
        public ModelUrl unconvert(String source) {
            return new ModelUrl(source);
        }
    }
}
