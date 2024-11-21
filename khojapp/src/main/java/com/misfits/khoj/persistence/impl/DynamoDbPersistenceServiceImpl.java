package com.misfits.khoj.persistence.impl;

import static com.misfits.khoj.constants.ApplicationConstants.*;

import com.misfits.khoj.exceptions.persitence.*;
import com.misfits.khoj.model.persistence.PersistenceKeys;
import com.misfits.khoj.model.persistence.UserProfileDetails;
import com.misfits.khoj.persistence.DynamoDbPersistenceService;
import com.misfits.khoj.utils.KhojUtils;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

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
      throw e;

    } catch (Exception e) {
      log.error("Unexpected error for user {}: {}", userId, e.getMessage());
      throw new UserPersistSaveException("Unexpected error while persisting user", e);
    }
  }

  @Override
  public boolean checkIfUserExists(String tableName, String userId) {
    Map<String, AttributeValue> keyToGet = new HashMap<>();
    keyToGet.put(ID, AttributeValue.builder().s(userId).build());

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

  @Override
  public void updateMapKey(
      String tableName,
      String primaryKey,
      String primaryKeyValue,
      String attributeName,
      String newValue) {

    Map<String, AttributeValue> key = new HashMap<>();
    key.put(primaryKey, AttributeValue.builder().s(primaryKeyValue).build());

    try {
      if (checkAttributeBeingUpdated(attributeName)) return;

      String updateExpression = "SET " + attributeName + " = :newValue";
      Map<String, AttributeValue> expressionValues = new HashMap<>();
      expressionValues.put(":newValue", AttributeValue.builder().s(newValue).build());

      UpdateItemRequest request =
          getUpdateItemRequest(tableName, key, updateExpression, expressionValues);
      dynamoDbClient.updateItem(request);

      log.info("Updated top-level attribute '{}' in table '{}'", attributeName, tableName);

    } catch (DynamoDbException e) {
      log.error(
          "Failed to update top-level attribute {}  for userId {} in table {} ",
          attributeName,
          primaryKeyValue,
          tableName,
          e);
      throw new DynamoDBUpdateException(
          "Exception Occurred while updating top-level attribute in DynamoDB table ", e);
    }
  }

  @Override
  public void updateMapKey(
      String tableName,
      String primaryKey,
      String primaryKeyValue,
      String mapAttributeName,
      String mapKey,
      String mapValue) {

    Map<String, AttributeValue> key = new HashMap<>();
    key.put(primaryKey, AttributeValue.builder().s(primaryKeyValue).build());
    try {
      if (checkAttributeBeingUpdated(mapAttributeName)) return;
      String initExpression =
          String.format(
              "SET %s = if_not_exists(%s, :emptyMap)", mapAttributeName, mapAttributeName);
      Map<String, AttributeValue> initValues = new HashMap<>();
      initValues.put(":emptyMap", AttributeValue.builder().m(new HashMap<>()).build());

      UpdateItemRequest initRequest =
          getUpdateItemRequest(tableName, key, initExpression, initValues);
      dynamoDbClient.updateItem(initRequest);

      String updateExpression = String.format("SET %s.#mapKey = :mapValue", mapAttributeName);
      Map<String, AttributeValue> expressionValues = new HashMap<>();
      expressionValues.put(":mapValue", AttributeValue.builder().s(mapValue).build());

      Map<String, String> expressionNames = new HashMap<>();
      expressionNames.put("#mapKey", mapKey);

      UpdateItemRequest updateRequest =
          getUpdateItemRequest(tableName, key, updateExpression, expressionValues, expressionNames);
      dynamoDbClient.updateItem(updateRequest);

      log.info("Updated nested attribute in table: {}", tableName);

    } catch (DynamoDbException e) {
      log.error(
          "Failed to update nested attribute {}  for userId {} in table {} ",
          mapKey,
          primaryKeyValue,
          tableName,
          e);
      throw new DynamoDBUpdateException(
          "Exception Occurred while updating nested attribute in DynamoDB table ", e);
    }
  }

  private static UpdateItemRequest getUpdateItemRequest(
      String tableName,
      Map<String, AttributeValue> key,
      String updateExpression,
      Map<String, AttributeValue> expressionValues,
      Map<String, String> expressionNames) {
    return UpdateItemRequest.builder()
        .tableName(tableName)
        .key(key)
        .updateExpression(updateExpression)
        .expressionAttributeValues(expressionValues)
        .expressionAttributeNames(expressionNames)
        .build();
  }

  private static UpdateItemRequest getUpdateItemRequest(
      String tableName,
      Map<String, AttributeValue> key,
      String initExpression,
      Map<String, AttributeValue> initValues) {
    return UpdateItemRequest.builder()
        .tableName(tableName)
        .key(key)
        .updateExpression(initExpression)
        .expressionAttributeValues(initValues)
        .build();
  }

  private static boolean checkAttributeBeingUpdated(String attributeName) {
    if (PersistenceKeys.USER_FILES.equals(attributeName)) {
      log.warn(
          "Update aborted: Only the 'files' attribute is allowed to be updated. Provided attribute key: {}",
          attributeName);
      return true;
    }
    return false;
  }
}
