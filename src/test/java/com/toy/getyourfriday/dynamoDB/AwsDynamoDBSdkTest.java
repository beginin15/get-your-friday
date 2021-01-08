package com.toy.getyourfriday.dynamoDB;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.domain.ModelUrl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = ModelUrlParser.class)
@Import(DynamoDBTestConfig.class)
public class AwsDynamoDBSdkTest {

    public static final String TABLE_NAME = "User";
    private static final String PK_ATTRIBUTE = "chatId";
    private static final String SECONDARY_INDEX_ATTRIBUTE = "monitoredUrl";

    private static AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private ModelUrlParser modelUrlParser;

    @Autowired
    private DynamoDB dynamoDB;

    private Integer chat_id;
    private ModelUrl modelUrl;

    // using constructor-autowired to inject bean to static field
    @Autowired
    public AwsDynamoDBSdkTest(AmazonDynamoDB amazonDynamoDB) {
        AwsDynamoDBSdkTest.amazonDynamoDB = amazonDynamoDB;
    }

    @BeforeEach
    public void setUp() {
        chat_id = 1234;
        modelUrl = modelUrlParser.findByName("lassie");
    }

    @Test
    @Order(1)
    @DisplayName("테이블 생성")
    void createTable() {
        // when
        Table table = dynamoDB.createTable(createUserTableRequest(TABLE_NAME, PK_ATTRIBUTE, SECONDARY_INDEX_ATTRIBUTE));

        // then
        TableDescription tableDescription = table.getDescription();
        assertThat(tableDescription.getTableStatus()).isEqualTo("ACTIVE");
        assertThat(tableDescription.getTableArn()).isEqualTo("arn:aws:dynamodb:ddblocal:000000000000:table/User");
        assertThat(tableDescription.getTableName()).isEqualTo("User");
        assertThat(tableDescription.getAttributeDefinitions().toString()).isEqualTo(String.format(
                "[{AttributeName: %s,AttributeType: N}, {AttributeName: %s,AttributeType: S}]",
                PK_ATTRIBUTE,
                SECONDARY_INDEX_ATTRIBUTE)
        );
        assertThat(tableDescription.getKeySchema().toString()).isEqualTo(
                "[{AttributeName: " + PK_ATTRIBUTE + ",KeyType: HASH}]"
        );

        GlobalSecondaryIndexDescription globalSecondaryIndexDescription = tableDescription.getGlobalSecondaryIndexes().get(0);
        assertThat(globalSecondaryIndexDescription.getIndexName()).isEqualTo("monitoredUrlIndex");
        assertThat(globalSecondaryIndexDescription.getKeySchema()).contains(
                new KeySchemaElement("monitoredUrl", KeyType.HASH)
        );
        assertThat(globalSecondaryIndexDescription.getProjection().getProjectionType()).isEqualTo("KEYS_ONLY");
        assertThat(tableDescription.getProvisionedThroughput().getReadCapacityUnits()).isEqualTo(1L);
        assertThat(tableDescription.getProvisionedThroughput().getWriteCapacityUnits()).isEqualTo(1L);
    }

    @Test
    @Order(2)
    @DisplayName("User 추가")
    void putItem() {
        // given
        Table table = dynamoDB.getTable(TABLE_NAME);

        Item item = new Item().withPrimaryKey(PK_ATTRIBUTE, chat_id)
                .withString(SECONDARY_INDEX_ATTRIBUTE, modelUrl.getUrl());

        // when
        PutItemResult putResult = table.putItem(item).getPutItemResult();

        // then
        assertThat(putResult.getSdkHttpMetadata().getHttpStatusCode()).isEqualTo(200);
    }

    @Test
    @Order(3)
    @DisplayName("User 조회")
    void getItem() {
        // given
        Table table = dynamoDB.getTable(TABLE_NAME);
        PrimaryKey key = new PrimaryKey(PK_ATTRIBUTE, chat_id);

        // when
        Item item = table.getItem(key);

        // then
        Item expected = new Item().withPrimaryKey("chatId", 1234)
                .withString("monitoredUrl", modelUrl.getUrl());
        assertThat(item).isEqualTo(expected);
    }

    @Test
    @Order(4)
    @DisplayName("User 수정")
    void updateItem() {
        // given
        Table table = dynamoDB.getTable(TABLE_NAME);
        AttributeUpdate attributeUpdate = new AttributeUpdate(SECONDARY_INDEX_ATTRIBUTE)
                .put(modelUrlParser.findByName("hawaiifive-o").getUrl());
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(PK_ATTRIBUTE, chat_id)
                .withAttributeUpdate(attributeUpdate)
                .withReturnValues(ReturnValue.ALL_NEW);

        // when
        UpdateItemOutcome updateResult = table.updateItem(updateItemSpec);

        // then
        Item expected = new Item().withPrimaryKey("chatId", 1234)
                .withString("monitoredUrl", "https://www.freitag.ch/en/f41?items=showall");

        assertThat(updateResult.getUpdateItemResult().getSdkHttpMetadata().getHttpStatusCode()).isEqualTo(200);
        assertThat(updateResult.getItem()).isEqualTo(expected);
    }

    @Test
    @Order(5)
    @DisplayName("User 삭제")
    void deleteItem() {
        // given
        Table table = dynamoDB.getTable(TABLE_NAME);
        PrimaryKey key = new PrimaryKey(PK_ATTRIBUTE, chat_id);

        // when
        DeleteItemOutcome deleteResult = table.deleteItem(key);

        // then
        assertThat(deleteResult.getDeleteItemResult().getSdkHttpMetadata().getHttpStatusCode()).isEqualTo(200);
        assertThat(table.getItem(key)).isNull();
    }

    public static CreateTableRequest createUserTableRequest(String tableName,
                                                            String keyAttributeName,
                                                            String secondaryAttributeName) {
        // define attributes
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition(keyAttributeName, ScalarAttributeType.N));
        attributeDefinitions.add(new AttributeDefinition(secondaryAttributeName, ScalarAttributeType.S));

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

    @AfterAll
    static void tearDown() {
        amazonDynamoDB.deleteTable(TABLE_NAME);
        amazonDynamoDB.shutdown();
    }
}