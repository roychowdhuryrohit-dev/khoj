package com.misfits.khoj.service.file;

import static com.misfits.khoj.constants.ApplicationConstants.*;
import static com.misfits.khoj.utils.KhojUtils.validateUserIdNotNull;

import com.misfits.khoj.config.AwsConfig;
import com.misfits.khoj.exceptions.file.FileListingException;
import com.misfits.khoj.exceptions.file.FileStandardizationException;
import com.misfits.khoj.exceptions.file.FileUploadException;
import com.misfits.khoj.exceptions.file.s3.InvalidPresignedUrlRequestException;
import com.misfits.khoj.exceptions.file.s3.PresignedUrlGenerationException;
import com.misfits.khoj.exceptions.file.s3.S3PresignedUrlException;
import com.misfits.khoj.model.file.FileUploadResponse;
import com.misfits.khoj.model.file.ListUserFilesResponse;
import com.misfits.khoj.model.file.MultipleFileUploadResponse;
import com.misfits.khoj.model.file.PreSignedUrlResponse;
import com.misfits.khoj.model.persistence.PersistenceKeys;
import com.misfits.khoj.persistence.DynamoDbPersistenceService;
import com.misfits.khoj.service.S3FileService;
import com.misfits.khoj.service.UserService;
import com.misfits.khoj.utils.KhojUtils;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@Slf4j
public class S3FileServiceImpl implements S3FileService {

  final AwsConfig awsConfig;

  final S3Client s3Client;

  final DynamoDbPersistenceService dynamoDbPersistenceService;

  final UserService userService;

  final S3Presigner s3Presigner;

  public S3FileServiceImpl(
      AwsConfig awsConfig,
      S3Client s3Client,
      DynamoDbPersistenceService dynamoDbPersistenceService,
      UserService userService,
      S3Presigner s3Presigner) {
    this.awsConfig = awsConfig;
    this.s3Client = s3Client;
    this.dynamoDbPersistenceService = dynamoDbPersistenceService;
    this.userService = userService;
    this.s3Presigner = s3Presigner;
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

    String key = getFileKey(standardizedFileName, userId);
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

      dynamoDbPersistenceService.updateMapKey(
          awsConfig.getDynamoDbTableName(),
          ID,
          userId,
          PersistenceKeys.USER_FILES.getKey(), //  top-level attribute key
          standardizedFileName, // Key within the map : fileName
          fileUploadResponse.getFileUrl() // Value for the fileName ket
          );

      log.info(
          "Updated DynamoDB record for userId {} with fileName {} in the key {}",
          userId,
          standardizedFileName,
          PersistenceKeys.USER_FILES.getKey());

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

  @Override
  public PreSignedUrlResponse getPresignedUrlForFile(String fileName, String userId) {

    validateUserIdNotNull(userId);

    log.info(
        "Received Request to generate Presigned URL for file {} for userId {}", fileName, userId);

    String fileKey = getFileKey(fileName, userId);

    try {
      GetObjectRequest getObjectRequest =
          GetObjectRequest.builder().bucket(awsConfig.getS3BucketName()).key(fileKey).build();

      GetObjectPresignRequest getObjectPresignRequest =
          GetObjectPresignRequest.builder()
              .signatureDuration(Duration.ofMinutes(PRESIGN_DURATION_MINUTES))
              .getObjectRequest(getObjectRequest)
              .build();

      PreSignedUrlResponse preSignedUrlResponse =
          getPreSignedUrlResponseObject(userId, getObjectPresignRequest);

      log.info("Generated Presigned URL for file {} for userId {}", fileName, userId);

      return preSignedUrlResponse;

    } catch (IllegalArgumentException ex) {
      log.error("Invalid arguments provided: fileName={}, userId={}", fileName, userId, ex);
      throw new InvalidPresignedUrlRequestException(
          "Invalid input parameters for generating presigned URL", ex);
    } catch (S3Exception ex) {
      log.error(
          "S3 exception while generating presigned URL for file: {} and userId: {}",
          fileName,
          userId,
          ex);
      throw new S3PresignedUrlException(
          "Error occurred while interacting with S3 to generate presigned URL", ex);
    } catch (Exception ex) {
      log.error(
          "Unexpected error while generating presigned URL for file: {} and userId: {}",
          fileName,
          userId,
          ex);
      throw new PresignedUrlGenerationException(
          "Unexpected error occurred while generating presigned URL", ex);
    }
  }

  @Override
  public List<String> generatePresignedUrlsForUserFiles(List<String> filenames, String userId) {
    validateUserIdNotNull(userId);
    log.info(
        "Received request to generate PreSigned URLs selected by userId {} for files {}",
        userId,
        filenames);

    return filenames.stream()
        .map(
            fileName -> {
              try {
                return getPresignedUrlForFile(fileName, userId).getPreSignedUrl();
              } catch (Exception ex) {
                log.error(
                    "Failed to generate PreSigned URL for file: {}. Skipping this file",
                    fileName,
                    ex);
                return null;
              }
            })
        .filter(Objects::nonNull)
        .toList();
  }

  private String getFileKey(String fileName, String userId) {
    return String.format("%s/%s/%s", awsConfig.getS3BaseDirectory(), userId, fileName);
  }

  private PreSignedUrlResponse getPreSignedUrlResponseObject(
      String userId, GetObjectPresignRequest getObjectPresignRequest) {
    PreSignedUrlResponse preSignedUrlResponse = new PreSignedUrlResponse();
    preSignedUrlResponse.setPreSignedUrl(
        s3Presigner.presignGetObject(getObjectPresignRequest).url().toString());
    preSignedUrlResponse.setUserId(userId);
    return preSignedUrlResponse;
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
