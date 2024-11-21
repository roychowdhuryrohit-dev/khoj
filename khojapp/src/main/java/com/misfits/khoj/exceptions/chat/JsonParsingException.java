package com.misfits.khoj.exceptions.chat;

public class JsonParsingException extends ChatServiceException {

  private static final String JSON_PARSING_ERROR = "JSON_PARSING_ERROR";

  public JsonParsingException(String message, Throwable cause) {
    super(message, JSON_PARSING_ERROR, cause);
  }

  public JsonParsingException(String message) {
    super(message, JSON_PARSING_ERROR);
  }

  public JsonParsingException(Throwable cause) {
    super(cause, JSON_PARSING_ERROR);
  }

  public JsonParsingException() {
    super(JSON_PARSING_ERROR);
  }

  public JsonParsingException(String message, JsonParsingException e) {
    super(message, JSON_PARSING_ERROR, e);
  }
}
