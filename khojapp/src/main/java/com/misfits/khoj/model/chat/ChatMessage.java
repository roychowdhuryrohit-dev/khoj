package com.misfits.khoj.model.chat;

public class ChatMessage {

  private String session_id;
  private String prompt;

  public ChatMessage(String session_id, String prompt) {
    this.session_id = session_id;
    this.prompt = prompt;
  }

  // Getters and Setters
  public String getSession_id() {
    return session_id;
  }

  public void setSession_id(String session_id) {
    this.session_id = session_id;
  }

  public String getPrompt() {
    return prompt;
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }

  @Override
  public String toString() {
    return "ChatMessage{" + "session_id='" + session_id + '\'' + ", prompt='" + prompt + '\'' + '}';
  }
}
