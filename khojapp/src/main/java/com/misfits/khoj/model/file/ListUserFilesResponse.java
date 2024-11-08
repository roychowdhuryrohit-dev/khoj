package com.misfits.khoj.model.file;

import com.misfits.khoj.model.user.BaseUserProfile;
import java.util.Map;

public class ListUserFilesResponse extends BaseUserProfile {

  private Map<String, String> files; // Map of fileKey to fileUrl

  public ListUserFilesResponse() {}

  public ListUserFilesResponse(Map<String, String> files) {
    this.files = files;
  }

  public Map<String, String> getFiles() {
    return files;
  }

  public void setFiles(Map<String, String> files) {
    this.files = files;
  }

  @Override
  public String toString() {
    return "UserFilesResponse{" + "files=" + files + '}';
  }
}
