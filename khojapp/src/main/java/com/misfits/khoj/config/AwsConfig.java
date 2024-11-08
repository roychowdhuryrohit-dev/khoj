package com.misfits.khoj.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AwsConfig {

  @Value("${aws.region}")
  String awsRegion;

  @Value("${aws.accessKey}")
  String accessKey;

  @Value("${aws.secretAccessKey}")
  String secretAccessKey;

  @Value("${aws.service.s3.bucketName}")
  String s3BucketName;

  @Value("${aws.service.s3.region}")
  String s3Region;

  @Value("${aws.service.s3.file.base-directory}")
  String s3BaseDirectory;

  @Value("${aws.service.dynamodb.region}")
  String dynamoDbRegion;

  @Value("${aws.service.cognito.user-pool-id}")
  String cognitoUserPoolId;
}
