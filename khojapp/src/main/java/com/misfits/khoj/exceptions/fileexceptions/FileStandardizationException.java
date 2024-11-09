package com.misfits.khoj.exceptions.fileexceptions;

import static com.misfits.khoj.constants.ApplicationConstants.FILE_STANDARDIZATION_ERROR;

public class FileStandardizationException extends FileExceptions {

  public FileStandardizationException(String message, Throwable cause) {
    super(message, FILE_STANDARDIZATION_ERROR, cause);
  }

  public FileStandardizationException(String message) {
    super(message, FILE_STANDARDIZATION_ERROR);
  }

  public FileStandardizationException(Throwable cause) {
    super("File standardization error occurred", FILE_STANDARDIZATION_ERROR, cause);
  }

  public FileStandardizationException() {
    super("File standardization error occurred", FILE_STANDARDIZATION_ERROR);
  }

  public FileStandardizationException(String message, FileStandardizationException e) {
    super(message, FILE_STANDARDIZATION_ERROR, e);
  }
}
