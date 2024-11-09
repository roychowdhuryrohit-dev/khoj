package com.misfits.khoj.exceptions.userexceptions;

public class UserProfileException extends RuntimeException {
  private final String errorCode;

  public UserProfileException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public UserProfileException(String message, String errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public UserProfileException(String errorCode) {
    super();
    this.errorCode = errorCode;
  }

  public UserProfileException(Throwable cause, String errorCode) {
    super(cause);
    this.errorCode = errorCode;
  }

  public UserProfileException() {
    super();
    this.errorCode = "USER_EXCEPTION";
  }

  public String getErrorCode() {
    return errorCode;
  }
}
