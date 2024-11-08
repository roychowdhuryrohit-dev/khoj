package com.misfits.khoj.model.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.misfits.khoj.model.user.BaseUserProfile;
import org.springframework.context.annotation.Configuration;

@Configuration
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadResponse extends BaseUserProfile {

  private String fileName; // Name of the uploaded file
  private String fileUrl; // URL of the file in S3 or another storage

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

  public FileUploadResponse(String fileName, String fileUrl) {
    this.fileName = fileName;
    this.fileUrl = fileUrl;
  }

  @Override
  public String toString() {
    return "SingleFileUploadResponse{"
        + "fileName='"
        + fileName
        + '\''
        + ", fileUrl='"
        + fileUrl
        + '\''
        + '}';
  }
}
