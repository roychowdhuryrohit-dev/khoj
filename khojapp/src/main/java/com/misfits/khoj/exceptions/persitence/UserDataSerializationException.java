package com.misfits.khoj.exceptions.persitence;

public class UserDataSerializationException extends DynamoDbBaseException {

  public UserDataSerializationException() {
    super();
  }

  public UserDataSerializationException(String message) {
    super(message);
  }

  public UserDataSerializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserDataSerializationException(Throwable cause) {
    super(cause);
  }

  public UserDataSerializationException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
