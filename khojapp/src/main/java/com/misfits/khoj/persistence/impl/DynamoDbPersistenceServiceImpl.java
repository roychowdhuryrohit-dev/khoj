package com.misfits.khoj.persistence.impl;

import static com.misfits.khoj.constants.ApplicationConstants.*;

import com.misfits.khoj.exceptions.persitence.UserDataSerializationException;
import com.misfits.khoj.exceptions.persitence.UserDataValidationException;
import com.misfits.khoj.exceptions.persitence.UserExistsCheckException;
import com.misfits.khoj.exceptions.persitence.UserPersistSaveException;
import com.misfits.khoj.model.persistence.UserProfileDetails;
import com.misfits.khoj.persistence.DynamoDbPersistenceService;
import com.misfits.khoj.utils.KhojUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Service
@Slf4j
public class DynamoDbPersistenceServiceImpl implements DynamoDbPersistenceService {

  private final DynamoDbClient dynamoDbClient;

  public DynamoDbPersistenceServiceImpl(DynamoDbClient dynamoDbClient) {
    this.dynamoDbClient = dynamoDbClient;
  }

  @Override
  public void saveUserProfileDetails(
      String tableName, String userId, UserProfileDetails userProfileDetails) {
    try {
      // Validate the user data
      if (userId == null || userId.isEmpty()) {
        throw new UserDataValidationException("User ID cannot be null or empty");
      }
      if (userProfileDetails == null) {
        throw new UserDataValidationException("UserProfileDetails cannot be null");
      }

      Map<String, AttributeValue> itemValues =
          KhojUtils.convertUserProfileToAttributeValueMap(userProfileDetails);
      itemValues.put(ID, AttributeValue.builder().s(userId).build());

      PutItemRequest request =
          PutItemRequest.builder().tableName(tableName).item(itemValues).build();

      dynamoDbClient.putItem(request);
      log.info("Successfully Saved User {} to Table {}", userId, tableName);

    } catch (DynamoDbException e) {
      log.error("Failed to persist user {} in DynamoDB: {}", userId, e.getMessage());
      throw new UserPersistSaveException("Error persisting user to DynamoDB", e);

    } catch (UserDataValidationException | UserDataSerializationException e) {
      throw e; // Rethrow to propagate the specific error

    } catch (Exception e) {
      log.error("Unexpected error for user {}: {}", userId, e.getMessage());
      throw new UserPersistSaveException("Unexpected error while persisting user", e);
    }
  }

  @Override
  public boolean checkIfUserExists(String tableName, String userId) {
    Map<String, AttributeValue> keyToGet = new HashMap<>();
    keyToGet.put("id", AttributeValue.builder().s(userId).build());

    GetItemRequest request = GetItemRequest.builder().tableName(tableName).key(keyToGet).build();

    try {
      Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();
      return returnedItem != null && !returnedItem.isEmpty();

    } catch (DynamoDbException e) {
      log.error(
          "DynamoDbException occurred while checking if user exists for userId {}: {}",
          userId,
          e.getMessage());
      throw new UserExistsCheckException("Error while checking if user exists in DynamoDB", e);

    } catch (Exception e) {
      log.error(
          "Unexpected exception occurred while checking if user exists for userId {}: {}",
          userId,
          e.getMessage());
      throw new UserExistsCheckException("Unexpected error while checking if user exists", e);
    }
  }
}
