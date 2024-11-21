package com.misfits.khoj.model.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiModuleResponse {

  // @JsonProperty("message")
  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
