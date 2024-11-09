package com.misfits.khoj.exceptions.fileexceptions;

import static com.misfits.khoj.constants.ApplicationConstants.FILE_UPLOAD_ERROR;

public class FileUploadException extends FileExceptions {

  public FileUploadException(String message, Throwable cause) {
    super(message, FILE_UPLOAD_ERROR, cause);
  }

  public FileUploadException(String message) {
    super(message, FILE_UPLOAD_ERROR);
  }

  public FileUploadException(Throwable cause) {
    super(cause, FILE_UPLOAD_ERROR);
  }

  public FileUploadException() {
    super(FILE_UPLOAD_ERROR);
  }

  public FileUploadException(String message, FileUploadException e) {
    super(message, FILE_UPLOAD_ERROR, e);
  }
}
