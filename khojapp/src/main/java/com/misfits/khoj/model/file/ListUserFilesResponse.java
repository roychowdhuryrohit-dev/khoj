package com.misfits.khoj.model.file;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListUserFilesResponse {
  private String userId;
  private Map<String, String> files; // Map of fileKey to fileUrl

  @Override
  public String toString() {
    return "UserFilesResponse{" + "userId='" + userId + '\'' + ", files=" + files + '}';
  }
}
