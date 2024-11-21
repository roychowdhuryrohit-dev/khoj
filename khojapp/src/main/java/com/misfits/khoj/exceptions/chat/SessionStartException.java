package com.misfits.khoj.exceptions.chat;

public class SessionStartException extends ChatServiceException {

  private static final String SESSION_START_ERROR = "SESSION_START_ERROR";

  public SessionStartException(String message, Throwable cause) {
    super(message, SESSION_START_ERROR, cause);
  }

  public SessionStartException(String message) {
    super(message, SESSION_START_ERROR);
  }

  public SessionStartException(Throwable cause) {
    super(cause, SESSION_START_ERROR);
  }

  public SessionStartException() {
    super(SESSION_START_ERROR);
  }

  public SessionStartException(String message, SessionStartException e) {
    super(message, SESSION_START_ERROR, e);
  }
}
