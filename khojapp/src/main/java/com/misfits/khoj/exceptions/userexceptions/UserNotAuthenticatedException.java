package com.misfits.khoj.exceptions.userexceptions;

public class UserNotAuthenticatedException extends UserProfileException {
  public UserNotAuthenticatedException(String message) {
    super(message);
  }

  // Constructor with message and cause
  public UserNotAuthenticatedException(String message, Throwable cause) {
    super(message, cause);
  }
}
