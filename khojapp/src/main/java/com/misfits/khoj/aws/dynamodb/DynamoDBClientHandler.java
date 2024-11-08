package com.misfits.khoj.aws.dynamodb;

import com.misfits.khoj.config.AwsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDBClientHandler {

  @Autowired AwsConfig awsConfig;

  @Bean
  public DynamoDbClient dynamoDbClient() {
    AwsCredentials awsCredentials =
        AwsBasicCredentials.create(awsConfig.getAccessKey(), awsConfig.getSecretAccessKey());

    return DynamoDbClient.builder()
        .region(Region.of(awsConfig.getDynamoDbRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }
}
