package com.misfits.khoj.model.file;

import com.misfits.khoj.model.user.BaseUserProfile;

public class PreSignedUrlResponse extends BaseUserProfile {

  private String preSignedUrl;

  public void setPreSignedUrl(String preSignedUrl) {
    this.preSignedUrl = preSignedUrl;
  }

  @Override
  public String toString() {
    return super.toString() + "preSignedUrl'=" + preSignedUrl + '\'';
  }

  public String getPreSignedUrl() {
    return preSignedUrl;
  }
}
