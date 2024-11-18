package com.misfits.khoj.model.chat;

import java.util.List;

public class AiModuleChatSessionRequest {

  private String sessionId;
  private List<String> fileUrls;

  public AiModuleChatSessionRequest(String sessionId, List<String> fileUrls) {
    this.sessionId = sessionId;
    this.fileUrls = fileUrls;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public List<String> getFileUrls() {
    return fileUrls;
  }

  public void setFileUrls(List<String> fileUrls) {
    this.fileUrls = fileUrls;
  }
}
