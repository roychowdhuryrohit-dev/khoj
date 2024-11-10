package com.misfits.khoj.model.persistence;

import static com.misfits.khoj.constants.ApplicationConstants.*;

import com.misfits.khoj.model.user.BaseUserProfile;
import java.util.HashMap;
import java.util.Map;

public class UserProfileDetails extends BaseUserProfile {
  String email;
  Boolean emailVerified;
  String name;
  private Map<String, String> files = new HashMap<>();

  public UserProfileDetails(Map<String, Object> attributes) {
    super.setUserId((String) attributes.get(SUB));
    this.email = (String) attributes.get(EMAIL);
    this.emailVerified = (Boolean) attributes.get(EMAIL_VERIFIED);
    this.name = (String) attributes.get(NAME);
  }

  public UserProfileDetails() {}

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getFiles() {
    return files;
  }

  public void setFiles(Map<String, String> files) {
    this.files = files;
  }
}
