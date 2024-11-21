package com.misfits.khoj.model.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ChatSessionRequest {

  @JsonProperty("filenames")
  private List<String> filenames;

  public List<String> getFilenames() {
    return filenames;
  }

  public void setFilenames(List<String> filenames) {
    this.filenames = filenames;
  }

  @Override
  public String toString() {
    return "ChatSessionRequest{" + "filenames=" + filenames + '}';
  }
}
