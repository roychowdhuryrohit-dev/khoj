package com.misfits.khoj.model;

import static com.misfits.khoj.constants.ApplicationConstants.*;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UserProfile {

  String sub;
  String email;
  Boolean emailVerified;
  String name;

  public UserProfile(Map<String, Object> attributes) {
    this.sub = (String) attributes.get(SUB);
    this.email = (String) attributes.get(EMAIL);
    this.emailVerified =
        attributes.get(EMAIL_VERIFIED) instanceof Boolean
            ? (Boolean) attributes.get(EMAIL_VERIFIED)
            : Boolean.parseBoolean((String) attributes.get(EMAIL_VERIFIED));
    this.name = (String) attributes.get(NAME);
  }

  @Override
  public String toString() {
    return "UserProfile{"
        + "userId='"
        + sub
        + '\''
        + ", email='"
        + email
        + '\''
        + ", emailVerified="
        + emailVerified
        + ", name='"
        + name
        + '\''
        + '}';
  }

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  public void setEmailVerified(Boolean emailVerified) {
    this.emailVerified = emailVerified;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
