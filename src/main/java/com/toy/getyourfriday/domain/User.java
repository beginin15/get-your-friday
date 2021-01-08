package com.toy.getyourfriday.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.Objects;

import static com.toy.getyourfriday.config.DynamoDBConfig.ModelUrlConverter;

@DynamoDBTable(tableName = "User")
public class User {

    @DynamoDBHashKey(attributeName = "chatId")
    private Integer chatId;

    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "byMonitoredUrl")
    @DynamoDBTypeConverted(converter = ModelUrlConverter.class)
    private ModelUrl monitoredUrl;

    // used for loading item by DBMapper
    public User() {}

    public User(Integer chatId, ModelUrl monitoredUrl) {
        this.chatId = chatId;
        this.monitoredUrl = monitoredUrl;
    }

    public User changeMonitoredUrl(ModelUrl modelUrl) {
        return new User(chatId, modelUrl);
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public ModelUrl getMonitoredUrl() {
        return monitoredUrl;
    }

    public void setMonitoredUrl(ModelUrl monitoredUrl) {
        this.monitoredUrl = monitoredUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getChatId().equals(user.getChatId()) &&
                getMonitoredUrl().equals(user.getMonitoredUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChatId(), getMonitoredUrl());
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", monitoredUrl=" + monitoredUrl +
                '}';
    }
}
