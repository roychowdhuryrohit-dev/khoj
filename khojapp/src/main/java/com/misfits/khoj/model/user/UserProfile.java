package com.misfits.khoj.model.user;

import static com.misfits.khoj.constants.ApplicationConstants.*;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UserProfile extends BaseUserProfile {

  String email;
  Boolean emailVerified;
  String name;

  public UserProfile(Map<String, Object> attributes) {
    super.setUserId((String) attributes.get(SUB));
    this.email = (String) attributes.get(EMAIL);
    this.emailVerified =
        attributes.get(EMAIL_VERIFIED) instanceof Boolean
            ? (Boolean) attributes.get(EMAIL_VERIFIED)
            : Boolean.parseBoolean((String) attributes.get(EMAIL_VERIFIED));
    this.name = (String) attributes.get(NAME);
  }

  @Override
  public String toString() {
    return super.toString()
        + "email='"
        + email
        + '\''
        + ", emailVerified="
        + emailVerified
        + ", name='"
        + name
        + '\'';
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
