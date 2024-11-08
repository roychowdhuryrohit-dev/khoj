package com.misfits.khoj.model.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileUploadResponse {

  private String fileName; // Name of the uploaded file
  private String fileUrl; // URL of the file in S3 or another storage
  private String userId; // Optional: user ID to indicate file ownership

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
