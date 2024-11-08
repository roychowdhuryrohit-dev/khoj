package com.misfits.khoj.model.file;

import com.misfits.khoj.model.user.BaseUserProfile;
import java.util.List;

public class MultipleFileUploadResponse extends BaseUserProfile {

  private int totalFiles;
  private List<FileUploadResponse> files;

  public MultipleFileUploadResponse() {}

  public MultipleFileUploadResponse(List<FileUploadResponse> files, int totalFiles) {
    this.files = files;
    this.totalFiles = totalFiles;
  }

  @Override
  public String toString() {
    return "MultiFileUploadResponse{" + "TotalFiles=" + totalFiles + ", files=" + files + '}';
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
