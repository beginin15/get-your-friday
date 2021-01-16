package com.toy.getyourfriday.dynamoDB;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndexDescription;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.config.DynamoDBConfig;
import com.toy.getyourfriday.domain.ModelUrl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        ModelUrlParser.class,
        DynamoDBConfig.class
})
public class AwsDynamoDBSdkTest {

    public static final String TABLE_NAME = "User";
    private static final String PK_ATTRIBUTE = "chatId";
    private static final String SECONDARY_INDEX_ATTRIBUTE = "monitoredUrl";

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDB dynamoDB;

    @Autowired
    private ModelUrlParser modelUrlParser;

    private Integer chatId;
    private ModelUrl modelUrl;
    private Item item;
    private Table table;

    @BeforeEach
    void setUp() {
        this.chatId = 1234;
        this.modelUrl = modelUrlParser.findByName("lassie");
        this.item = new Item().withPrimaryKey(PK_ATTRIBUTE, chatId)
                .withString(SECONDARY_INDEX_ATTRIBUTE, modelUrl.getUrl());
        this.table = dynamoDB.createTable(createUserTableRequest(TABLE_NAME, PK_ATTRIBUTE, SECONDARY_INDEX_ATTRIBUTE));
    }

    @Test
    @DisplayName("생성된 테이블 확인")
    void createTable() {
        TableDescription tableDescription = table.getDescription();
        assertThat(tableDescription.getTableStatus())
                .isEqualTo("ACTIVE");
        assertThat(tableDescription.getTableArn())
                .isEqualTo("arn:aws:dynamodb:ddblocal:000000000000:table/User");
        assertThat(tableDescription.getTableName())
                .isEqualTo("User");
        assertThat(tableDescription.getAttributeDefinitions().toString())
                .isEqualTo(String.format(
                        "[{AttributeName: %s,AttributeType: N}, {AttributeName: %s,AttributeType: S}]",
                        PK_ATTRIBUTE,
                        SECONDARY_INDEX_ATTRIBUTE)
                );
        assertThat(tableDescription.getKeySchema().toString())
                .isEqualTo("[{AttributeName: " + PK_ATTRIBUTE + ",KeyType: HASH}]");

        GlobalSecondaryIndexDescription globalSecondaryIndexDescription =
                tableDescription.getGlobalSecondaryIndexes().get(0);
        assertThat(globalSecondaryIndexDescription.getIndexName())
                .isEqualTo("monitoredUrlIndex");
        assertThat(globalSecondaryIndexDescription.getKeySchema())
                .contains(new KeySchemaElement("monitoredUrl", KeyType.HASH));
        assertThat(globalSecondaryIndexDescription.getProjection().getProjectionType())
                .isEqualTo("KEYS_ONLY");
        assertThat(tableDescription.getProvisionedThroughput().getReadCapacityUnits())
                .isEqualTo(1L);
        assertThat(tableDescription.getProvisionedThroughput().getWriteCapacityUnits())
                .isEqualTo(1L);
    }

    @Test
    @DisplayName("User 추가")
    void putItem() {
        // when
        PutItemResult putResult = table.putItem(item).getPutItemResult();

        // then
        assertThat(putResult.getSdkHttpMetadata().getHttpStatusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("User 조회")
    void getItem() {
        // given
        table.putItem(item).getPutItemResult();

        // when
        Item savedItem = table.getItem(new PrimaryKey(PK_ATTRIBUTE, chatId));

        // then
        Item expected = new Item().withPrimaryKey("chatId", chatId)
                .withString("monitoredUrl", modelUrl.getUrl());
        assertThat(savedItem).isEqualTo(expected);
    }

    @Test
    @DisplayName("User 수정")
    void updateItem() {
        // given
        table.putItem(item);
        final String updatedModelName = "hawaiifive-o";
        AttributeUpdate attributeUpdate = new AttributeUpdate(SECONDARY_INDEX_ATTRIBUTE)
                .put(modelUrlParser.findByName(updatedModelName).getUrl());
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(PK_ATTRIBUTE, chatId)
                .withAttributeUpdate(attributeUpdate)
                .withReturnValues(ReturnValue.ALL_NEW);

        // when
        UpdateItemOutcome updateResult = table.updateItem(updateItemSpec);

        // then
        Item expected = new Item().withPrimaryKey("chatId", chatId)
                .withString("monitoredUrl", modelUrlParser.findByName(updatedModelName).getUrl());

        assertThat(updateResult.getUpdateItemResult().getSdkHttpMetadata().getHttpStatusCode())
                .isEqualTo(200);
        assertThat(updateResult.getItem()).isEqualTo(expected);
    }

    @Test
    @DisplayName("User 삭제")
    void deleteItem() {
        // given
        table.putItem(item);
        PrimaryKey key = new PrimaryKey(PK_ATTRIBUTE, chatId);

        // when
        DeleteItemOutcome deleteResult = table.deleteItem(key);

        // then
        assertThat(deleteResult.getDeleteItemResult().getSdkHttpMetadata().getHttpStatusCode())
                .isEqualTo(200);
        assertThat(table.getItem(key)).isNull();
    }

    public static CreateTableRequest createUserTableRequest(String tableName,
                                                            String keyAttributeName,
                                                            String secondaryAttributeName) {
        // define attributes
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(
                new AttributeDefinition(keyAttributeName, ScalarAttributeType.N)
        );
        attributeDefinitions.add(
                new AttributeDefinition(secondaryAttributeName, ScalarAttributeType.S)
        );

        // define the PK schema - "chatId"
        KeySchemaElement keySchema = new KeySchemaElement(keyAttributeName, KeyType.HASH);

        // setting throughput
        ProvisionedThroughput throughput = new ProvisionedThroughput(1L, 1L);

        // define global secondary index - "monitoredUrl"
        String secondaryIndex = secondaryAttributeName + "Index";
        GlobalSecondaryIndex globalSecondaryIndex = new GlobalSecondaryIndex().withIndexName(secondaryIndex)
                .withKeySchema(new KeySchemaElement(secondaryAttributeName, KeyType.HASH))
                .withProjection(new Projection().withProjectionType("KEYS_ONLY"))
                .withProvisionedThroughput(throughput);

        // construct create table request
        return new CreateTableRequest()
                .withTableName(tableName)
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(keySchema)
                .withGlobalSecondaryIndexes(globalSecondaryIndex)
                .withProvisionedThroughput(throughput);
    }

    @AfterEach
    void tearDown() {
        amazonDynamoDB.deleteTable(TABLE_NAME);
    }
}
