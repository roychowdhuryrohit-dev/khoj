package com.misfits.khoj.aws.cognito;

import com.misfits.khoj.config.AwsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class CognitoClientHandler {

  final AwsConfig awsConfig;

  public CognitoClientHandler(AwsConfig awsConfig) {
    this.awsConfig = awsConfig;
  }

  @Bean
  public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
    AwsCredentials awsCredentials =
        AwsBasicCredentials.create(awsConfig.getAccessKey(), awsConfig.getSecretAccessKey());

    return CognitoIdentityProviderClient.builder()
        .region(Region.of(awsConfig.getAwsRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }
}
