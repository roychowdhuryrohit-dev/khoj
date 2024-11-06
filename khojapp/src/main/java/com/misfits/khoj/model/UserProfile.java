package com.misfits.khoj.model;

import static com.misfits.khoj.constants.ApplicationConstants.*;

import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
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
}
