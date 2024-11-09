package com.misfits.khoj.exceptions.userexceptions;

public class UserNotAuthenticatedException extends UserProfileException {
  public UserNotAuthenticatedException(String message) {
    super(message);
  }

  public UserNotAuthenticatedException(String message, Throwable cause) {
    super(message, String.valueOf(cause));
  }
}
