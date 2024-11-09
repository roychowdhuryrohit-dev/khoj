package com.misfits.khoj.exceptions;

import com.misfits.khoj.exceptions.fileexceptions.FileListingException;
import com.misfits.khoj.exceptions.fileexceptions.FileUploadException;
import com.misfits.khoj.exceptions.persitence.*;
import com.misfits.khoj.exceptions.userexceptions.MissingUserAttributeException;
import com.misfits.khoj.exceptions.userexceptions.UserNotAuthenticatedException;
import com.misfits.khoj.exceptions.userexceptions.UserProfileException;
import com.misfits.khoj.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(UserNotAuthenticatedException.class)
  public ResponseEntity<ErrorResponse> handleUserNotAuthenticated(
      UserNotAuthenticatedException ex, HttpServletRequest request) {
    logger.error("UserNotAuthenticatedException occurred:", ex);
    return buildErrorResponse(
        HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(MissingUserAttributeException.class)
  public ResponseEntity<ErrorResponse> handleMissingUserAttribute(
      MissingUserAttributeException ex, HttpServletRequest request) {
    logger.error("MissingUserAttributeException occurred:", ex);
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(UserProfileException.class)
  public ResponseEntity<ErrorResponse> handleUserProfileException(
      UserProfileException ex, HttpServletRequest request) {
    logger.error("UserProfileException occurred:", ex);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "User Profile Error",
        ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(FileListingException.class)
  public ResponseEntity<ErrorResponse> handleFileListingException(
      FileListingException e, HttpServletRequest request) {
    logger.error("FileListingException occurred:", e);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "File Listing Error",
        e.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(FileUploadException.class)
  public ResponseEntity<ErrorResponse> handleFileUploadException(
      FileUploadException e, HttpServletRequest request) {
    logger.error("FileUploadException occurred:", e);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "File Upload Error",
        e.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception e, HttpServletRequest request) {
    logger.error("Exception occurred:", e);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal Server Error",
        e.getMessage(),
        request.getRequestURI());
  }

  // Custom DynamoDB exception handlers

  @ExceptionHandler(UserPersistSaveException.class)
  public ResponseEntity<ErrorResponse> handleUserPersistSaveException(
      UserPersistSaveException ex, HttpServletRequest request) {
    logger.error("UserPersistSaveException occurred:", ex);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Persistence Error",
        ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(UserDataSerializationException.class)
  public ResponseEntity<ErrorResponse> handleUserDataSerializationException(
      UserDataSerializationException ex, HttpServletRequest request) {
    logger.error("UserDataSerializationException occurred:", ex);
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "Serialization Error", ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(UserExistsCheckException.class)
  public ResponseEntity<ErrorResponse> handleUserDataSerializationException(
      UserExistsCheckException ex, HttpServletRequest request) {
    logger.error("UserExistsCheckException occurred:", ex);
    return buildErrorResponse(
        HttpStatus.NOT_FOUND, "Existence Check Error", ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(UserDataValidationException.class)
  public ResponseEntity<ErrorResponse> handleUserDataValidationException(
      UserDataValidationException ex, HttpServletRequest request) {
    logger.error("UserDataValidationException occurred:", ex);
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "Validation Error", ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(DynamoDbBaseException.class)
  public ResponseEntity<ErrorResponse> handleDynamoDbBaseException(
      DynamoDbBaseException ex, HttpServletRequest request) {
    logger.error("DynamoDbBaseException occurred:", ex);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "DynamoDB Error",
        ex.getMessage(),
        request.getRequestURI());
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus status, String error, String message, String path) {
    ErrorResponse errorResponse = new ErrorResponse(status.value(), error, message, path);
    return new ResponseEntity<>(errorResponse, status);
  }
}
