package com.toy.getyourfriday.domain.user;

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

import java.util.List;

import static com.toy.getyourfriday.dynamoDB.AwsDynamoDBSdkTest.TABLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

@SpringBootTest(classes = {
        ModelUrlParser.class,
        DynamoDBConfig.class
})
class UserRepositoryTest {

    public static final String MODEL_NAME = "lassie";

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

    private User user;

    @BeforeEach
    void createTable() {
        TableUtils.createTableIfNotExists(amazonDynamoDB, userTableRequest);
        this.user = new User(1234, modelUrlParser.findByName(MODEL_NAME));
    }

    @Test
    @DisplayName("User 추가")
    void saveItem() {
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
        Integer id = userRepository.save(user).getChatId();

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
        Integer id = userRepository.save(user).getChatId();

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
        User savedUser = userRepository.save(user);

        // when
        userRepository.delete(savedUser);

        // then
        thenThrownBy(() -> userRepository.findById(1234)
                .orElseThrow(() -> new UserNotFoundException(1234)))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("ModelUrl 조회")
    void findByModelUrl() {
        // given
        User savedUser = userRepository.save(user);

        // when
        List<User> users = userRepository.findByMonitoredUrl(savedUser.getMonitoredUrl());

        // then
        assertThat(users.size()).isEqualTo(1);
        assertThat(users).containsExactly(new User(1234, modelUrlParser.findByName("lassie")));
    }

    @Test
    @DisplayName("count 쿼리 - 결과 1개")
    void countReturnOne() {
        // given
        User savedUser = userRepository.save(user);

        // when
        Integer count = userRepository.countByMonitoredUrl(savedUser.getMonitoredUrl());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("count 쿼리 - 결과 2개")
    void countReturnTwo() {
        // given
        User savedUserA = userRepository.save(user);
        User savedUserB = userRepository.save(new User(5678, modelUrlParser.findByName(MODEL_NAME)));

        // when
        Integer count = userRepository.countByMonitoredUrl(savedUserA.getMonitoredUrl());

        // then
        assertThat(count).isEqualTo(2);
        assertThat(savedUserA).isNotEqualTo(savedUserB);
    }

    @AfterEach
    void deleteTable() {
        amazonDynamoDB.deleteTable(TABLE_NAME);
    }
}
