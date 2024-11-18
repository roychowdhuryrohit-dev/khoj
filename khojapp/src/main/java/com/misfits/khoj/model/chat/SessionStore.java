package com.misfits.khoj.model.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class SessionStore {

  private final Map<String, String> sessionMap = new ConcurrentHashMap<>(); // userId -> sessionId

  public void saveSession(String userId, String sessionId) {
    sessionMap.put(userId, sessionId);
  }

  public String getSessionId(String userId) {
    return sessionMap.get(userId);
  }

  public void clearSession(String userId) {
    sessionMap.remove(userId);
  }
}
