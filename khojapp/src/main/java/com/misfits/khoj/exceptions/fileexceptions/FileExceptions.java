package com.misfits.khoj.exceptions.fileexceptions;

import lombok.Getter;

@Getter
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

  public String getErrorCode() {
    return errorCode;
  }
}
