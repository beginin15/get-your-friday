package com.toy.getyourfriday.domain;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.toy.getyourfriday.component.ModelUrlParser;
import com.toy.getyourfriday.config.DynamoDBConfig;
import com.toy.getyourfriday.exception.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.toy.getyourfriday.dynamoDB.AwsDynamoDBSdkTest.TABLE_NAME;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@SpringBootTest(classes = {
        ModelUrlParser.class,
        DynamoDBConfig.class
})
class UserRepositoryTest {

    @Autowired
    private ModelUrlParser modelUrlParser;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateTableRequest userTableRequest;

    @BeforeEach
    void createTable() {
        TableUtils.createTableIfNotExists(amazonDynamoDB, userTableRequest);
    }

    @Test
    @DisplayName("User 추가")
    void saveItem() {
        // given
        User user = new User(1234, modelUrlParser.findByName("lassie"));

        // when
        User savedUser = userRepository.save(user);

        // then
        then(savedUser)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("chatId", 1234)
                .hasFieldOrPropertyWithValue("monitoredUrl", modelUrlParser.findByName("lassie"));
    }

    @Test
    @DisplayName("User 조회")
    void getItem() {
        // given
        Integer id = userRepository.save(
                new User(1234, modelUrlParser.findByName("lassie"))
        ).getChatId();

        // when
        User retrievedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        then(retrievedUser)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("chatId", 1234)
                .hasFieldOrPropertyWithValue("monitoredUrl", modelUrlParser.findByName("lassie"));
    }

    @Test
    @DisplayName("User 수정")
    void updateItem() {
        // given
        Integer id = userRepository.save(
                new User(1234, modelUrlParser.findByName("lassie"))
        ).getChatId();

        User retrievedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        retrievedUser = retrievedUser.changeMonitoredUrl(modelUrlParser.findByName("dexter"));

        // when
        User updatedUser = userRepository.save(retrievedUser);

        // then
        then(updatedUser)
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("chatId", 1234)
                .hasFieldOrPropertyWithValue("monitoredUrl", modelUrlParser.findByName("dexter"));
    }

    @Test
    @DisplayName("User 삭제")
    void deleteItem() {
        // given
        User user = userRepository.save(new User(1234, modelUrlParser.findByName("lassie")));

        // when
        userRepository.delete(user);

        // then
        thenThrownBy(() -> userRepository.findById(user.getChatId())
                .orElseThrow(() -> new UserNotFoundException(user.getChatId())))
                .isInstanceOf(UserNotFoundException.class);
    }

    @AfterEach
    void deleteTable() {
        amazonDynamoDB.deleteTable(TABLE_NAME);
    }
}
