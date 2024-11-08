package com.misfits.khoj.exceptions.fileexceptions;

public class FileStandardizationException extends FileExceptions {
  public FileStandardizationException(String message) {
    super(message, "FILE_STANDARDIZATION_ERROR");
  }
}
