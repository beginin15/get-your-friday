package com.toy.getyourfriday.web;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TableCreationRunner implements ApplicationRunner {

    private final AmazonDynamoDB amazonDynamoDB;

    private final CreateTableRequest createUserTableRequest;

    @Autowired
    public TableCreationRunner(AmazonDynamoDB amazonDynamoDB, CreateTableRequest createUserTableRequest) {
        this.amazonDynamoDB = amazonDynamoDB;
        this.createUserTableRequest = createUserTableRequest;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TableUtils.createTableIfNotExists(amazonDynamoDB, createUserTableRequest);
    }
}
