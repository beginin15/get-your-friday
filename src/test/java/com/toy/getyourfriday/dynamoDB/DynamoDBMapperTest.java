package com.toy.getyourfriday.dynamoDB;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.config.DynamoDBConfig;
import com.toy.getyourfriday.domain.user.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.toy.getyourfriday.dynamoDB.AwsDynamoDBSdkTest.TABLE_NAME;
import static com.toy.getyourfriday.dynamoDB.AwsDynamoDBSdkTest.createUserTableRequest;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {
        ModelUrlParser.class,
        DynamoDBConfig.class
})
public class DynamoDBMapperTest {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private ModelUrlParser modelUrlParser;

    @BeforeAll
    void createTable() {
        TableUtils.createTableIfNotExists(amazonDynamoDB, createUserTableRequest(
                TABLE_NAME,
                "chatId",
                "monitoredUrl")
        );
    }

    @Test
    @DisplayName("User 추가")
    void putItem() {
        // given
        User user = new User(1234, modelUrlParser.findByName("lassie"));

        // when
        dynamoDBMapper.save(user);

        assertThat(dynamoDBMapper.load(User.class, 1234)).isNotNull();
    }

    @Test
    @DisplayName("User 조회")
    void getItem() {
        // given
        User user = new User(1234, modelUrlParser.findByName("lassie"));
        dynamoDBMapper.save(user);

        // when
        User actual = dynamoDBMapper.load(User.class, 1234);

        // then
        assertThat(actual).isEqualTo(new User(1234, modelUrlParser.findByName("lassie")));
    }

    @Test
    @DisplayName("User 수정")
    void updateItem() {
        // given
        User user = new User(1234, modelUrlParser.findByName("lassie"));
        dynamoDBMapper.save(user);
        User loaded = dynamoDBMapper.load(User.class, 1234);

        // when
        User updated = loaded.changeMonitoredUrl(modelUrlParser.findByName("dexter"));
        dynamoDBMapper.save(updated);

        // then
        User actual = dynamoDBMapper.load(User.class, 1234);
        assertThat(actual).isEqualTo(new User(1234, modelUrlParser.findByName("dexter")));
    }

    @Test
    @DisplayName("User 제거")
    void deleteUser() {
        // given
        User user = new User(1234, modelUrlParser.findByName("lassie"));
        dynamoDBMapper.save(user);

        // when
        dynamoDBMapper.delete(user);

        // then
        User deleted = dynamoDBMapper.load(User.class, 1234);
        assertThat(deleted).isNull();
    }

    @AfterAll
    void tearDown() {
        amazonDynamoDB.deleteTable(TABLE_NAME);
    }
}


