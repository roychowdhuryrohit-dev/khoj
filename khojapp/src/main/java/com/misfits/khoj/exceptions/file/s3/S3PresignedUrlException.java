package com.misfits.khoj.exceptions.file.s3;

import static com.misfits.khoj.constants.ApplicationConstants.S3_PRESIGNED_URL_ERROR;

import com.misfits.khoj.exceptions.file.FileExceptions;

public class S3PresignedUrlException extends FileExceptions {

  public S3PresignedUrlException(String message, Throwable cause) {
    super(message, S3_PRESIGNED_URL_ERROR, cause);
  }

  public S3PresignedUrlException(String message) {
    super(message, S3_PRESIGNED_URL_ERROR);
  }

  public S3PresignedUrlException(Throwable cause) {
    super(cause, S3_PRESIGNED_URL_ERROR);
  }

  public S3PresignedUrlException() {
    super(S3_PRESIGNED_URL_ERROR);
  }

  public S3PresignedUrlException(String message, S3PresignedUrlException e) {
    super(message, S3_PRESIGNED_URL_ERROR, e);
  }
}
