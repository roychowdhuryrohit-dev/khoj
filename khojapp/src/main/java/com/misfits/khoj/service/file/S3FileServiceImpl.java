package com.misfits.khoj.service.file;

import static com.misfits.khoj.constants.ApplicationConstants.S3_BASE_URL;
import static com.misfits.khoj.utils.KhojUtils.validateUserIdNotNull;

import com.misfits.khoj.config.AwsConfig;
import com.misfits.khoj.exceptions.fileexceptions.FileListingException;
import com.misfits.khoj.exceptions.fileexceptions.FileStandardizationException;
import com.misfits.khoj.exceptions.fileexceptions.FileUploadException;
import com.misfits.khoj.model.file.FileUploadResponse;
import com.misfits.khoj.model.file.ListUserFilesResponse;
import com.misfits.khoj.model.file.MultipleFileUploadResponse;
import com.misfits.khoj.persistence.DynamoDbPersistenceService;
import com.misfits.khoj.service.S3FileService;
import com.misfits.khoj.service.UserService;
import com.misfits.khoj.utils.KhojUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@Service
@Slf4j
public class S3FileServiceImpl implements S3FileService {

  final AwsConfig awsConfig;

  final S3Client s3Client;

  final DynamoDbPersistenceService dynamoDbPersistenceService;

  final UserService userService;

  public S3FileServiceImpl(
      AwsConfig awsConfig,
      S3Client s3Client,
      DynamoDbPersistenceService dynamoDbPersistenceService,
      UserService userService) {
    this.awsConfig = awsConfig;
    this.s3Client = s3Client;
    this.dynamoDbPersistenceService = dynamoDbPersistenceService;
    this.userService = userService;
  }

  @Override
  public FileUploadResponse uploadFile(MultipartFile file, String userId) {
    validateUserIdNotNull(userId);
    FileUploadResponse fileUploadResponse = new FileUploadResponse();
    String standardizedFileName;

    try {
      standardizedFileName = KhojUtils.standardizeFileName(file.getOriginalFilename());
    } catch (FileStandardizationException e) {
      log.error("Failed to standardize file name for user {}: {}", userId, e.getMessage());
      throw e;
    }

    log.info(
        "Uploading file {} with Standardized Name {} into bucket {}",
        file.getOriginalFilename(),
        standardizedFileName,
        awsConfig.getS3BucketName());

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
    validateUserIdNotNull(userId);
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

    return getMultipleFileUploadResponse(userId, fileResponses);
  }

  @Override
  public ListUserFilesResponse listUserFiles(String userId) {
    validateUserIdNotNull(userId);
    if (!dynamoDbPersistenceService.checkIfUserExists(awsConfig.getDynamoDbTableName(), userId)) {
      log.info(
          "Saving User to Database {} for userId {}", awsConfig.getDynamoDbTableName(), userId);
      dynamoDbPersistenceService.saveUserProfileDetails(
          awsConfig.getDynamoDbTableName(),
          userId,
          userService.fetchUserDetails(awsConfig.getCognitoUserPoolId(), userId));
    } else {
      log.info("User already exists in Database for userId {}", userId);
    }

    String directoryPathPrefix = String.format("%s/%s/", awsConfig.getS3BaseDirectory(), userId);
    log.info(
        "Retrieving all files for UserId {} from storage {}", userId, awsConfig.getS3BucketName());

    ListObjectsV2Request request =
        ListObjectsV2Request.builder()
            .bucket(awsConfig.getS3BucketName())
            .prefix(directoryPathPrefix)
            .build();
    ListUserFilesResponse listUserFilesResponse = new ListUserFilesResponse();
    listUserFilesResponse.setUserId(userId);
    try {
      Map<String, String> files =
          s3Client.listObjectsV2(request).contents().stream()
              .collect(
                  Collectors.toMap(
                      s3Object ->
                          s3Object
                              .key()
                              .substring(
                                  directoryPathPrefix.length()), // Extract only the file name
                      s3Object ->
                          String.format(S3_BASE_URL, awsConfig.getS3BucketName(), s3Object.key())));

      listUserFilesResponse.setFiles(files);

    } catch (S3Exception e) {
      log.error(
          "Failed to retrieve files for userId {}: {}", userId, e.awsErrorDetails().errorMessage());
      throw new FileListingException("Failed to list files from S3 for userId:  " + userId, e);
    }
    log.info("Successfully retrieved all available files for userId {}", userId);
    return listUserFilesResponse;
  }

  private static MultipleFileUploadResponse getMultipleFileUploadResponse(
      String userId, List<FileUploadResponse> fileResponses) {
    // Create and populate MultipleFileUploadResponse
    MultipleFileUploadResponse multipleFileUploadResponse = new MultipleFileUploadResponse();
    multipleFileUploadResponse.setUserId(userId);
    multipleFileUploadResponse.setTotalFiles(fileResponses.size());
    multipleFileUploadResponse.setFiles(fileResponses);
    return multipleFileUploadResponse;
  }
}
