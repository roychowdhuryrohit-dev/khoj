package com.misfits.khoj.exceptions.fileexceptions;

public class FileUploadException extends FileExceptions {
  public FileUploadException(String message, Throwable cause) {
    super(message, "FILE_UPLOAD_ERROR", cause);
  }
}
