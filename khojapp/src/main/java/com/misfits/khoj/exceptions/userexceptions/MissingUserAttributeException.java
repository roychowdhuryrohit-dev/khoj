package com.misfits.khoj.exceptions.userexceptions;

public class MissingUserAttributeException extends UserProfileException {
  public MissingUserAttributeException(String message) {
    super(message);
  }

  // Constructor with message and cause
  public MissingUserAttributeException(String message, Throwable cause) {
    super(message, cause);
  }
}
