package com.misfits.khoj.exceptions;

import com.misfits.khoj.exceptions.fileexceptions.FileListingException;
import com.misfits.khoj.exceptions.fileexceptions.FileUploadException;
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

  // Handle ResourceNotFoundException
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
  }

  // Handle InvalidRequestException
  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRequest(
      InvalidRequestException ex, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
  }

  // Handle UserNotAuthenticatedException
  @ExceptionHandler(UserNotAuthenticatedException.class)
  public ResponseEntity<ErrorResponse> handleUserNotAuthenticated(
      UserNotAuthenticatedException ex, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage(), request.getRequestURI());
  }

  // Handle MissingUserAttributeException
  @ExceptionHandler(MissingUserAttributeException.class)
  public ResponseEntity<ErrorResponse> handleMissingUserAttribute(
      MissingUserAttributeException ex, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
  }

  // Handle UserProfileException specifically
  @ExceptionHandler(UserProfileException.class)
  public ResponseEntity<ErrorResponse> handleUserProfileException(
      UserProfileException ex, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "User Profile Error",
        ex.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(FileListingException.class)
  public ResponseEntity<ErrorResponse> handleFileListingException(
      FileListingException e, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "File Listing Error",
        e.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(FileUploadException.class)
  public ResponseEntity<ErrorResponse> handleFileUploadException(
      FileUploadException e, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "File Upload Error",
        e.getMessage(),
        request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception e, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal Server Error",
        e.getMessage(),
        request.getRequestURI());
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus status, String error, String message, String path) {
    ErrorResponse errorResponse = new ErrorResponse(status.value(), error, message, path);
    return new ResponseEntity<>(errorResponse, status);
  }
}
