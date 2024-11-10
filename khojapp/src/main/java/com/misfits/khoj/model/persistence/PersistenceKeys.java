package com.misfits.khoj.model.persistence;

public enum PersistenceKeys {
  USER_FILES("files");

  private final String key;

  PersistenceKeys(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
