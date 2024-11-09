package com.misfits.khoj.exceptions.persitence;

public class UserDataValidationException extends DynamoDbBaseException {

  public UserDataValidationException() {
    super();
  }

  public UserDataValidationException(String message) {
    super(message);
  }

  public UserDataValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserDataValidationException(Throwable cause) {
    super(cause);
  }

  public UserDataValidationException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
