package com.misfits.khoj.exceptions.fileexceptions;

public class FileListingException extends FileExceptions {
  public FileListingException(String message, Throwable cause) {
    super(message, "FILE_DOWNLOAD_ERROR", cause);
  }
}
