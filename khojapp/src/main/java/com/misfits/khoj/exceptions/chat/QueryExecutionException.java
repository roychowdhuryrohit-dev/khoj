package com.misfits.khoj.exceptions.chat;

public class QueryExecutionException extends ChatServiceException {

  private static final String QUERY_EXECUTION_ERROR = "QUERY_EXECUTION_ERROR";

  public QueryExecutionException(String message, Throwable cause) {
    super(message, QUERY_EXECUTION_ERROR, cause);
  }

  public QueryExecutionException(String message) {
    super(message, QUERY_EXECUTION_ERROR);
  }

  public QueryExecutionException(Throwable cause) {
    super(cause, QUERY_EXECUTION_ERROR);
  }

  public QueryExecutionException() {
    super(QUERY_EXECUTION_ERROR);
  }

  public QueryExecutionException(String message, QueryExecutionException e) {
    super(message, QUERY_EXECUTION_ERROR, e);
  }
}
