package com.misfits.khoj.controller;

import com.misfits.khoj.model.file.FileUploadResponse;
import com.misfits.khoj.model.file.ListUserFilesResponse;
import com.misfits.khoj.model.file.MultipleFileUploadResponse;
import com.misfits.khoj.service.S3FileService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class FileController {

  @Autowired private S3FileService s3FileService;

  @PostMapping("/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
    FileUploadResponse fileUploadResponse =
        s3FileService.uploadFile(file, extractUserIdFromJwt(jwt));
    return ResponseEntity.ok(fileUploadResponse);
  }

  @PostMapping("/uploadMultiple")
  public ResponseEntity<MultipleFileUploadResponse> uploadFiles(
      @RequestParam("files") List<MultipartFile> files, @AuthenticationPrincipal Jwt jwt) {
    MultipleFileUploadResponse multipleFileUploadResponse =
        s3FileService.uploadFiles(files, extractUserIdFromJwt(jwt));
    return ResponseEntity.ok(multipleFileUploadResponse);
  }

  @GetMapping("/listUserFiles")
  public ResponseEntity<ListUserFilesResponse> listUserFiles(@AuthenticationPrincipal Jwt jwt) {
    ListUserFilesResponse response = s3FileService.listUserFiles(extractUserIdFromJwt(jwt));
    return ResponseEntity.ok(response);
  }

  // Private helper method to extract user ID from JWT
  private String extractUserIdFromJwt(Jwt jwt) {
    if (jwt == null) {
      return null; // Return null if JWT is missing
    }
    return jwt.getClaim("sub");
  }
}
