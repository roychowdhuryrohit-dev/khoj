package com.misfits.khoj.model.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.annotation.Configuration;

@Configuration
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadResponse {

  private String fileName; // Name of the uploaded file
  private String fileUrl; // URL of the file in S3 or another storage
  private String userId; // Optional: user ID to indicate file ownership

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getFileUrl() {
    return fileUrl;
  }

  public void setFileUrl(String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public FileUploadResponse() {}

  public FileUploadResponse(String fileName, String userId, String fileUrl) {
    this.fileName = fileName;
    this.userId = userId;
    this.fileUrl = fileUrl;
  }

  @Override
  public String toString() {
    return "SingleFileUploadResponse{"
        + "userId='"
        + userId
        + '\''
        + ", fileName='"
        + fileName
        + '\''
        + ", fileUrl='"
        + fileUrl
        + '\''
        + '}';
  }
}
