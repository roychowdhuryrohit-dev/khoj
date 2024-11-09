package com.misfits.khoj.persistence;

import com.misfits.khoj.model.persistence.UserProfileDetails;

public interface DynamoDbPersistenceService {

  void saveUserProfileDetails(
      String tableName, String userId, UserProfileDetails userProfileDetails);

  boolean checkIfUserExists(String tableName, String userId);
}
