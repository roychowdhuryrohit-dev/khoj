package com.misfits.khoj.model.chat;

import java.util.List;

public class ChatSessionRequest {
  private List<String> filenames;

  public List<String> getFilenames() {
    return filenames;
  }

  public void setFilenames(List<String> filenames) {
    this.filenames = filenames;
  }
}
