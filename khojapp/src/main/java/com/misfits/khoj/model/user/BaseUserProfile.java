package com.misfits.khoj.model.user;

public abstract class BaseUserProfile {
  private String userId;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "userId='" + userId + +'\'';
  }
}
