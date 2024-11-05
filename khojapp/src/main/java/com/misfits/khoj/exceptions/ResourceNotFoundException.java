package com.misfits.khoj.exceptions;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }

  // Constructor with message and cause
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  // Constructor with cause
  public ResourceNotFoundException(Throwable cause) {
    super(cause);
  }
}
