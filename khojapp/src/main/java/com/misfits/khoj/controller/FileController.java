package com.misfits.khoj.controller;

import com.misfits.khoj.model.file.FileUploadResponse;
import com.misfits.khoj.model.file.ListUserFilesResponse;
import com.misfits.khoj.model.file.MultipleFileUploadResponse;
import com.misfits.khoj.model.file.PreSignedUrlResponse;
import com.misfits.khoj.service.S3FileService;
import com.misfits.khoj.service.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class FileController {

  private final S3FileService s3FileService;

  private final UserService userService;

  public FileController(UserService userService, S3FileService s3FileService) {
    this.userService = userService;
    this.s3FileService = s3FileService;
  }

  @PostMapping("/upload")
  public ResponseEntity<FileUploadResponse> uploadFile(
      @RequestParam("file") MultipartFile file, @AuthenticationPrincipal OAuth2User principal) {
    FileUploadResponse fileUploadResponse =
        s3FileService.uploadFile(file, userService.getUserId(principal));
    return ResponseEntity.ok(fileUploadResponse);
  }

  @PostMapping("/uploadMultiple")
  public ResponseEntity<MultipleFileUploadResponse> uploadFiles(
      @RequestParam("files") List<MultipartFile> files,
      @AuthenticationPrincipal OAuth2User principal) {
    MultipleFileUploadResponse multipleFileUploadResponse =
        s3FileService.uploadFiles(files, userService.getUserId(principal));
    return ResponseEntity.ok(multipleFileUploadResponse);
  }

  @GetMapping("/listUserFiles")
  public ResponseEntity<ListUserFilesResponse> listUserFiles(
      @AuthenticationPrincipal OAuth2User principal) {
    ListUserFilesResponse response = s3FileService.listUserFiles(userService.getUserId(principal));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/getPreSignedUrl")
  public ResponseEntity<PreSignedUrlResponse> getPresignedUrlForFile(
      @RequestParam("fileName") String fileName, @AuthenticationPrincipal OAuth2User principal) {
    PreSignedUrlResponse preSignedUrlResponse =
        s3FileService.getPresignedUrlForFile(fileName, userService.getUserId(principal));
    return ResponseEntity.ok(preSignedUrlResponse);
  }
}
