package com.misfits.khoj.service;

import com.misfits.khoj.model.chat.AiModuleResponse;
import java.util.List;

public interface ChatService {
  AiModuleResponse startSession(String sessionId, List<String> fileUrls);

  AiModuleResponse sendQuery(String sessionId, String prompt);
}
