package com.misfits.khoj.model.file;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultipleFileUploadResponse {

  private String userId;
  private int totalFiles;
  private List<FileUploadResponse> files;

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
}
