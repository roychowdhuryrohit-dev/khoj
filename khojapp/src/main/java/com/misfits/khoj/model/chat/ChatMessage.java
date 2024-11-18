package com.misfits.khoj.model.chat;

public class ChatMessage {

  private String sessionId;
  private String prompt;

  // Getters and Setters
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getPrompt() {
    return prompt;
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }
}
