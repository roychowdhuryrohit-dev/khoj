package com.misfits.khoj.exceptions.persitence;

public class DynamoDBUpdateException extends DynamoDbBaseException {

  public DynamoDBUpdateException() {
    super();
  }

  public DynamoDBUpdateException(String message) {
    super(message);
  }

  public DynamoDBUpdateException(String message, Throwable cause) {
    super(message, cause);
  }

  public DynamoDBUpdateException(Throwable cause) {
    super(cause);
  }

  public DynamoDBUpdateException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
