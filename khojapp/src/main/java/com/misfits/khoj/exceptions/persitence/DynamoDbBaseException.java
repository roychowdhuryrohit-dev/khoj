package com.misfits.khoj.exceptions.persitence;

public class DynamoDbBaseException extends RuntimeException {

  public DynamoDbBaseException() {
    super();
  }

  public DynamoDbBaseException(String message) {
    super(message);
  }

  public DynamoDbBaseException(String message, Throwable cause) {
    super(message, cause);
  }

  public DynamoDbBaseException(Throwable cause) {
    super(cause);
  }

  public DynamoDbBaseException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
