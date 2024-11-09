package com.misfits.khoj.exceptions.file;

import static com.misfits.khoj.constants.ApplicationConstants.FILE_LISTING_ERROR;

public class FileListingException extends FileExceptions {

  public FileListingException(String message, Throwable cause) {
    super(message, FILE_LISTING_ERROR, cause);
  }

  public FileListingException(String message) {
    super(message, FILE_LISTING_ERROR);
  }

  public FileListingException(Throwable cause) {
    super(cause, FILE_LISTING_ERROR);
  }

  public FileListingException() {
    super(FILE_LISTING_ERROR);
  }

  public FileListingException(String message, FileListingException e) {
    super(message, FILE_LISTING_ERROR, e);
  }
}
