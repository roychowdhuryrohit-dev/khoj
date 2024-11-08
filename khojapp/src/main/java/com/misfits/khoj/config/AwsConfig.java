package com.misfits.khoj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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

  public String getAccessKey() {
    return accessKey;
  }

  public String getAwsRegion() {
    return awsRegion;
  }

  public String getSecretAccessKey() {
    return secretAccessKey;
  }

  public String getS3BucketName() {
    return s3BucketName;
  }

  public String getS3Region() {
    return s3Region;
  }

  public String getS3BaseDirectory() {
    return s3BaseDirectory;
  }

  public String getDynamoDbRegion() {
    return dynamoDbRegion;
  }

  public String getCognitoUserPoolId() {
    return cognitoUserPoolId;
  }
}
