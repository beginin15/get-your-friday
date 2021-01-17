package com.toy.getyourfriday.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.toy.getyourfriday.dto.RegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static com.toy.getyourfriday.config.DynamoDBConfig.ModelUrlConverter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@DynamoDBTable(tableName = "User")
public class User {

    @DynamoDBHashKey(attributeName = "chatId")
    private Integer chatId;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "byMonitoredUrl")
    @DynamoDBTypeConverted(converter = ModelUrlConverter.class)
    private ModelUrl monitoredUrl;

    public User changeMonitoredUrl(ModelUrl modelUrl) {
        return new User(chatId, modelUrl);
    }

    public static User from(RegisterRequest registerRequest) {
        return User.builder()
                .chatId(registerRequest.getChatId())
                .monitoredUrl(registerRequest.getModelUrl())
                .build();
    }
}
