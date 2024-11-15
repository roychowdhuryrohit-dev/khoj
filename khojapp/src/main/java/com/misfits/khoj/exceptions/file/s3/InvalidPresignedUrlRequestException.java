package com.misfits.khoj.exceptions.file.s3;

import static com.misfits.khoj.constants.ApplicationConstants.INVALID_PRESIGNED_URL_REQUEST;

import com.misfits.khoj.exceptions.file.FileExceptions;

public class InvalidPresignedUrlRequestException extends FileExceptions {

  public InvalidPresignedUrlRequestException(String message, Throwable cause) {
    super(message, INVALID_PRESIGNED_URL_REQUEST, cause);
  }

  public InvalidPresignedUrlRequestException(String message) {
    super(message, INVALID_PRESIGNED_URL_REQUEST);
  }

  public InvalidPresignedUrlRequestException(Throwable cause) {
    super(cause, INVALID_PRESIGNED_URL_REQUEST);
  }

  public InvalidPresignedUrlRequestException() {
    super(INVALID_PRESIGNED_URL_REQUEST);
  }

  public InvalidPresignedUrlRequestException(
      String message, InvalidPresignedUrlRequestException e) {
    super(message, INVALID_PRESIGNED_URL_REQUEST, e);
  }
}
