package com.toy.getyourfriday.web;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class TableDeletionRunner implements ApplicationListener<ContextClosedEvent> {

    private final AmazonDynamoDB amazonDynamoDB;

    private final CreateTableRequest createTableRequest;

    @Autowired
    public TableDeletionRunner(AmazonDynamoDB amazonDynamoDB, CreateTableRequest createTableRequest) {
        this.amazonDynamoDB = amazonDynamoDB;
        this.createTableRequest = createTableRequest;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        final String TABLE_NAME = createTableRequest.getTableName();
        TableUtils.deleteTableIfExists(amazonDynamoDB, new DeleteTableRequest().withTableName(TABLE_NAME));
    }
}
