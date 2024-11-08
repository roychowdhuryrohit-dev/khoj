package com.misfits.khoj.model.file;

import java.util.List;

public class MultipleFileUploadResponse {

  private String userId;
  private int totalFiles;
  private List<FileUploadResponse> files;

  public MultipleFileUploadResponse() {}

  public MultipleFileUploadResponse(String userId, List<FileUploadResponse> files, int totalFiles) {
    this.userId = userId;
    this.files = files;
    this.totalFiles = totalFiles;
  }

  @Override
  public String toString() {
    return "MultiFileUploadResponse{"
        + "userId='"
        + userId
        + '\''
        + ", totalFiles="
        + totalFiles
        + ", files="
        + files
        + '}';
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getTotalFiles() {
    return totalFiles;
  }

  public void setTotalFiles(int totalFiles) {
    this.totalFiles = totalFiles;
  }

  public List<FileUploadResponse> getFiles() {
    return files;
  }

  public void setFiles(List<FileUploadResponse> files) {
    this.files = files;
  }
}
