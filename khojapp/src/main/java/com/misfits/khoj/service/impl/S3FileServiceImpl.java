package com.misfits.khoj.service.impl;

import static com.misfits.khoj.constants.ApplicationConstants.S3_BASE_URL;

import com.misfits.khoj.config.AwsConfig;
import com.misfits.khoj.exceptions.fileexceptions.FileListingException;
import com.misfits.khoj.exceptions.fileexceptions.FileStandardizationException;
import com.misfits.khoj.exceptions.fileexceptions.FileUploadException;
import com.misfits.khoj.model.file.FileUploadResponse;
import com.misfits.khoj.model.file.ListUserFilesResponse;
import com.misfits.khoj.model.file.MultipleFileUploadResponse;
import com.misfits.khoj.service.S3FileService;
import com.misfits.khoj.utils.KhojUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@Service
@Slf4j
public class S3FileServiceImpl implements S3FileService {

  @Autowired AwsConfig awsConfig;

  @Autowired S3Client s3Client;

  @Override
  public FileUploadResponse uploadFile(MultipartFile file, String userId) {

    FileUploadResponse fileUploadResponse = new FileUploadResponse();
    String standardizedFileName;

    try {
      standardizedFileName = KhojUtils.standardizeFileName(file.getOriginalFilename());
    } catch (FileStandardizationException e) {
      log.error("Failed to standardize file name for user {}: {}", userId, e.getMessage());
      throw e;
    }

    String key =
        String.format("%s/%s/%s", awsConfig.getS3BaseDirectory(), userId, standardizedFileName);
    try {
      s3Client.putObject(
          PutObjectRequest.builder().bucket(awsConfig.getS3BucketName()).key(key).build(),
          RequestBody.fromBytes(file.getBytes()));

      fileUploadResponse.setUserId(userId);
      fileUploadResponse.setFileName(standardizedFileName);
      fileUploadResponse.setFileUrl(String.format(S3_BASE_URL, awsConfig.getS3BucketName(), key));

      log.info(
          "Uploaded file {} to bucket {} for userId {}",
          standardizedFileName,
          awsConfig.getS3BucketName(),
          userId);

    } catch (Exception e) {
      // Handle both IOException and S3Exception with a single catch block
      String errorMessage =
          (e instanceof S3Exception s3Exception)
              ? s3Exception.awsErrorDetails().errorMessage()
              : e.getMessage();

      log.error(
          "File upload failed for file {} for user {}: {}",
          standardizedFileName,
          userId,
          errorMessage);
      throw new FileUploadException("File upload failed: " + errorMessage, e);
    }

    return fileUploadResponse;
  }

  @Override
  public MultipleFileUploadResponse uploadFiles(List<MultipartFile> files, String userId) {
    log.info("Uploading Multiple Files into bucket {}", awsConfig.getS3BucketName());
    // List to store each file's upload response
    List<FileUploadResponse> fileResponses =
        files.stream()
            .map(
                file -> {
                  FileUploadResponse response = uploadFile(file, userId);
                  response.setUserId(null); // Nullify userId for each response
                  return response;
                })
            .toList();

    // Create and populate MultipleFileUploadResponse
    MultipleFileUploadResponse multipleFileUploadResponse = new MultipleFileUploadResponse();
    multipleFileUploadResponse.setUserId(userId);
    multipleFileUploadResponse.setTotalFiles(fileResponses.size());
    multipleFileUploadResponse.setFiles(fileResponses);

    return multipleFileUploadResponse;
  }

  @Override
  public ListUserFilesResponse listUserFiles(String userId) {
    String prefix = String.format("%s/%s/", awsConfig.getS3BaseDirectory(), userId);

    log.info(
        "Retrieving all files for user {} from bucket {}", userId, awsConfig.getS3BucketName());

    ListObjectsV2Request request =
        ListObjectsV2Request.builder().bucket(awsConfig.getS3BucketName()).prefix(prefix).build();
    ListUserFilesResponse listUserFilesResponse = new ListUserFilesResponse();
    listUserFilesResponse.setUserId(userId);
    try {
      // Attempt to list objects from S3
      Map<String, String> files =
          s3Client.listObjectsV2(request).contents().stream()
              .collect(
                  Collectors.toMap(
                      s3Object ->
                          s3Object.key().substring(prefix.length()), // Extract only the file name
                      s3Object ->
                          String.format(S3_BASE_URL, awsConfig.getS3BucketName(), s3Object.key())));

      listUserFilesResponse.setFiles(files);

    } catch (S3Exception e) {
      // Log and throw a FileListingException for S3-specific issues
      log.error("Failed to list files for user {}: {}", userId, e.awsErrorDetails().errorMessage());
      throw new FileListingException("Failed to list files from S3 for user " + userId, e);
    }
    log.info("Successfully retrieved file list for user {}", userId);
    return listUserFilesResponse;
  }
}
