package com.misfits.khoj.exceptions.userexceptions;

public class UserProfileException extends RuntimeException {
  public UserProfileException(String message) {
    super(message);
  }

  public UserProfileException(String message, Throwable cause) {
    super(message, cause);
  }
}
