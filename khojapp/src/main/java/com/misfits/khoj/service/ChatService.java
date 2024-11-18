package com.misfits.khoj.service;

import java.util.List;

public interface ChatService {
  String startSession(String sessionId, List<String> fileUrls);

  String sendQuery(String sessionId, String prompt);
}
