package com.misfits.khoj.model.file;

import java.util.Map;

public class ListUserFilesResponse {

  private String userId;
  private Map<String, String> files; // Map of fileKey to fileUrl

  public ListUserFilesResponse() {}

  public ListUserFilesResponse(String userId, Map<String, String> files) {
    this.userId = userId;
    this.files = files;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Map<String, String> getFiles() {
    return files;
  }

  public void setFiles(Map<String, String> files) {
    this.files = files;
  }

  @Override
  public String toString() {
    return "UserFilesResponse{" + "userId='" + userId + '\'' + ", files=" + files + '}';
  }
}
