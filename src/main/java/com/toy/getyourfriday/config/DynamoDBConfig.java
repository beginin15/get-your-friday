package com.toy.getyourfriday.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.toy.getyourfriday.domain.scraping.ModelUrl;
import com.toy.getyourfriday.domain.user.User;
import com.toy.getyourfriday.domain.user.UserRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = UserRepository.class)
public class DynamoDBConfig {

    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;

    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;

    @Value("${amazon.aws.dynamodb.endpoint}")
    private String dynamoDBEndpoint;

    public AWSCredentialsProvider amazonAWSCredentialsProvider() {
        return new AWSStaticCredentialsProvider(amazonAWSCredentials());
    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
    }

    @Bean
    @Primary
    public DynamoDBMapperConfig dynamoDBMapperConfig() {
        return DynamoDBMapperConfig.DEFAULT;
    }

    @Bean
    @Primary
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB, DynamoDBMapperConfig config) {
        return new DynamoDBMapper(amazonDynamoDB, config);
    }

    @Bean
    public EndpointConfiguration endpointConfiguration() {
        return new EndpointConfiguration(dynamoDBEndpoint, Regions.AP_NORTHEAST_2.getName());
    }

    @Profile("dev")
    @Bean(name = "amazonDynamoDB")
    public AmazonDynamoDB localAmazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withCredentials(amazonAWSCredentialsProvider())
                .withEndpointConfiguration(endpointConfiguration())
                .build();
    }

    @Profile("!dev")
    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder.standard()
                .withCredentials(InstanceProfileCredentialsProvider.getInstance())
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
    }

    @Profile("dev")
    @Bean
    public DynamoDB dynamoDB() {
        return new DynamoDB(localAmazonDynamoDB());
    }

    @Bean
    public CreateTableRequest createUserTableRequest(DynamoDBMapper dynamoDBMapper) {
        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(1L, 1L);
        CreateTableRequest createTableRequest = dynamoDBMapper.generateCreateTableRequest(User.class)
                .withProvisionedThroughput(provisionedThroughput);
        createTableRequest.getGlobalSecondaryIndexes().forEach(v -> v.setProvisionedThroughput(provisionedThroughput));
        return createTableRequest;
    }

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
