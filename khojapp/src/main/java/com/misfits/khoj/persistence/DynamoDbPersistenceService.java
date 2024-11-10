package com.misfits.khoj.persistence;

import com.misfits.khoj.model.persistence.UserProfileDetails;

public interface DynamoDbPersistenceService {

  void saveUserProfileDetails(
      String tableName, String userId, UserProfileDetails userProfileDetails);

  boolean checkIfUserExists(String tableName, String userId);

  void updateMapKey(
      String tableName,
      String primaryKey,
      String primaryKeyValue,
      String attributeName,
      String value);

  void updateMapKey(
      String tableName,
      String primaryKey,
      String primaryKeyValue,
      String mapAttributeName,
      String mapKey,
      String mapValue);
}
