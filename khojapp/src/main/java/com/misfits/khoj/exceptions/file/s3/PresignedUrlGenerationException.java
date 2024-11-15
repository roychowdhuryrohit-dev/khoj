package com.misfits.khoj.exceptions.file.s3;

import static com.misfits.khoj.constants.ApplicationConstants.PRESIGNED_URL_GEN_ERROR;

import com.misfits.khoj.exceptions.file.FileExceptions;

public class PresignedUrlGenerationException extends FileExceptions {

  public PresignedUrlGenerationException(String message, Throwable cause) {
    super(message, PRESIGNED_URL_GEN_ERROR, cause);
  }

  public PresignedUrlGenerationException(String message) {
    super(message, PRESIGNED_URL_GEN_ERROR);
  }

  public PresignedUrlGenerationException(Throwable cause) {
    super(cause, PRESIGNED_URL_GEN_ERROR);
  }

  public PresignedUrlGenerationException() {
    super(PRESIGNED_URL_GEN_ERROR);
  }

  public PresignedUrlGenerationException(String message, PresignedUrlGenerationException e) {
    super(message, PRESIGNED_URL_GEN_ERROR, e);
  }
}
