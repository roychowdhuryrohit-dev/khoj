package com.misfits.khoj.exceptions.file;

public class FileExceptions extends RuntimeException {

  private final String errorCode;

  public FileExceptions(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public FileExceptions(String message, String errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public FileExceptions(String errorCode) {
    super();
    this.errorCode = errorCode;
  }

  public FileExceptions(Throwable cause, String errorCode) {
    super(cause);
    this.errorCode = errorCode;
  }

  public FileExceptions() {
    super();
    this.errorCode = "FILE_EXCEPTION";
  }

  public String getErrorCode() {
    return errorCode;
  }
}
