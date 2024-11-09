package com.misfits.khoj.exceptions.persitence;

public class UserPersistSaveException extends DynamoDbBaseException {

  public UserPersistSaveException() {
    super();
  }

  public UserPersistSaveException(String message) {
    super(message);
  }

  public UserPersistSaveException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserPersistSaveException(Throwable cause) {
    super(cause);
  }

  public UserPersistSaveException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
