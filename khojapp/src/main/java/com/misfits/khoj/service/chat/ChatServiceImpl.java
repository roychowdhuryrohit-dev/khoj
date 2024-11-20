package com.misfits.khoj.service.chat;

import com.misfits.khoj.model.chat.AiModuleChatSessionRequest;
import com.misfits.khoj.model.chat.AiModuleResponse;
import com.misfits.khoj.model.chat.ChatMessage;
import com.misfits.khoj.service.ChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatServiceImpl implements ChatService {

  private final RestTemplate restTemplate;

  @Value("${python.module.url}")
  private String pythonModuleUrl;

  public ChatServiceImpl(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public AiModuleResponse startSession(String sessionId, List<String> fileUrls) {
    AiModuleChatSessionRequest request =
        new AiModuleChatSessionRequest(sessionId, fileUrls); // Include userId if needed

    ResponseEntity<String> response =
        restTemplate.postForEntity(pythonModuleUrl + "/startSession", request, String.class);

    AiModuleResponse aiModuleResponse = new AiModuleResponse(response.getBody());

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      return aiModuleResponse;
    } else {
      throw new RuntimeException("Failed to start session with Python module");
    }
  }

  public AiModuleResponse sendQuery(String sessionId, String prompt) {
    ChatMessage request = new ChatMessage();
    request.setSession_id(sessionId);
    request.setPrompt(prompt);

    ResponseEntity<String> response =
        restTemplate.postForEntity(pythonModuleUrl + "/sendQuery", request, String.class);

    AiModuleResponse aiModuleResponse = new AiModuleResponse(response.getBody());

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      return aiModuleResponse;
    } else {
      throw new RuntimeException("Failed to send query to Python module");
    }
  }
}
