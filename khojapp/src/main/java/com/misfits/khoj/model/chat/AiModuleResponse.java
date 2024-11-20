package com.misfits.khoj.model.chat;

public class AiModuleResponse {
  private String message;

  public AiModuleResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
