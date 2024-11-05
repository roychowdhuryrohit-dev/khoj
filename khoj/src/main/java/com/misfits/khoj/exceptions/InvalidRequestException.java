package com.misfits.khoj.exceptions;

public class InvalidRequestException extends RuntimeException {
  public InvalidRequestException(String message) {
    super(message);
  }

  // Constructor with message and cause
  public InvalidRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  // Constructor with cause
  public InvalidRequestException(Throwable cause) {
    super(cause);
  }
}
