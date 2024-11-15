package com.misfits.khoj.aws.s3;

import com.misfits.khoj.config.AwsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3ClientHandler {

  final AwsConfig awsConfig;

  public S3ClientHandler(AwsConfig awsConfig) {
    this.awsConfig = awsConfig;
  }

  @Bean
  public S3Client s3Client() {
    AwsCredentials awsCredentials =
        AwsBasicCredentials.create(awsConfig.getAccessKey(), awsConfig.getSecretAccessKey());

    return S3Client.builder()
        .region(Region.of(awsConfig.getS3Region()))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    AwsCredentials awsCredentials =
        AwsBasicCredentials.create(awsConfig.getAccessKey(), awsConfig.getSecretAccessKey());

    return S3Presigner.builder()
        .region(Region.of(awsConfig.getS3Region()))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }
}
