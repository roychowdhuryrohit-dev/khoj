package com.misfits.khoj.model.chat;

import java.util.List;

public class AiModuleChatSessionRequest {

  private String session_id;
  private List<String> file_urls;

  public AiModuleChatSessionRequest(String session_id, List<String> file_urls) {
    this.session_id = session_id;
    this.file_urls = file_urls;
  }

  public String getSession_id() {
    return session_id;
  }

  public void setSession_id(String session_id) {
    this.session_id = session_id;
  }

  public List<String> getFile_urls() {
    return file_urls;
  }

  public void setFile_urls(List<String> file_urls) {
    this.file_urls = file_urls;
  }
}
