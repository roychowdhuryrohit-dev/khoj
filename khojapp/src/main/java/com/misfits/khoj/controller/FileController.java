package com.misfits.khoj.controller;

import com.misfits.khoj.model.file.FileUploadResponse;
import com.misfits.khoj.model.file.ListUserFilesResponse;
import com.misfits.khoj.model.file.MultipleFileUploadResponse;
import com.misfits.khoj.service.S3FileService;
import com.misfits.khoj.service.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class FileController {

  @Autowired private S3FileService s3FileService;

  @Autowired UserService userService;

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
}
