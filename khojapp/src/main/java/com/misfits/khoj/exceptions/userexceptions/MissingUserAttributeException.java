package com.misfits.khoj.exceptions.userexceptions;

public class MissingUserAttributeException extends UserProfileException {
  public MissingUserAttributeException(String message) {
    super(message);
  }

  public MissingUserAttributeException(String message, Throwable cause) {
    super(message, String.valueOf(cause));
  }

  public MissingUserAttributeException(String message, String message1) {
    super(message);
  }
}
