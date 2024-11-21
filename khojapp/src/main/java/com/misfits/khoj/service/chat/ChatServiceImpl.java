package com.misfits.khoj.service.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  private final ObjectMapper objectMapper;

  public ChatServiceImpl(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
    this.restTemplate = restTemplateBuilder.build();
    this.objectMapper = objectMapper;
  }

  public AiModuleResponse startSession(String sessionId, List<String> fileUrls) {
    AiModuleChatSessionRequest request =
        new AiModuleChatSessionRequest(sessionId, fileUrls); // Include userId if needed

    ResponseEntity<String> response =
        restTemplate.postForEntity(pythonModuleUrl + "/startSession", request, String.class);

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      try {
        return objectMapper.readValue(response.getBody(), AiModuleResponse.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to parse JSON response while session from AI module", e);
      }
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

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      try {
        return objectMapper.readValue(response.getBody(), AiModuleResponse.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to parse JSON response while quering from AI module", e);
      }
    } else {
      throw new RuntimeException("Failed to send query to Python module");
    }
  }
}
