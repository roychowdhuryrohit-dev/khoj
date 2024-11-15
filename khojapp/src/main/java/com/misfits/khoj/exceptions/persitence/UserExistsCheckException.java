package com.misfits.khoj.exceptions.persitence;

public class UserExistsCheckException extends DynamoDbBaseException {

  public UserExistsCheckException() {
    super();
  }

  public UserExistsCheckException(String message) {
    super(message);
  }

  public UserExistsCheckException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserExistsCheckException(Throwable cause) {
    super(cause);
  }

  public UserExistsCheckException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
