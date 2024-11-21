package com.misfits.khoj.exceptions.chat;

public class ChatServiceException extends RuntimeException {

  private final String errorCode;

  public ChatServiceException(String message, String errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public ChatServiceException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public ChatServiceException(Throwable cause, String errorCode) {
    super(cause);
    this.errorCode = errorCode;
  }

  public ChatServiceException(String errorCode) {
    super();
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
