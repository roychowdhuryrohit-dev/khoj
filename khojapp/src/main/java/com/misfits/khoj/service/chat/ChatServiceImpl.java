package com.misfits.khoj.service.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.misfits.khoj.exceptions.chat.JsonParsingException;
import com.misfits.khoj.exceptions.chat.QueryExecutionException;
import com.misfits.khoj.exceptions.chat.SessionStartException;
import com.misfits.khoj.model.chat.AiModuleChatSessionRequest;
import com.misfits.khoj.model.chat.AiModuleResponse;
import com.misfits.khoj.model.chat.ChatMessage;
import com.misfits.khoj.service.ChatService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
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

    AiModuleChatSessionRequest request = new AiModuleChatSessionRequest(sessionId, fileUrls);
    log.info(
        "Received request to start session with sessionId {} with fileUrls : {}",
        request.getSession_id(),
        request.getFile_urls());

    try {
      log.info("Sending request to AI module to start chat session for sessionId: {}", sessionId);
      ResponseEntity<String> response =
          restTemplate.postForEntity(
              "https://784f-2601-647-6881-9160-14cf-974e-375a-fbdb.ngrok-free.app"
                  + "/startSession",
              request,
              String.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        log.info(
            "Received session response from AI module for sessionId: {} with body {}",
            sessionId,
            response.getBody());
        return objectMapper.readValue(response.getBody(), AiModuleResponse.class);
      } else {
        throw new SessionStartException(
            "Failed to start session with AI module. Response code: " + response.getStatusCode());
      }
    } catch (JsonProcessingException jsonException) {
      log.error(
          "JSON parsing failed while receiving response from AI Module for startSession: {}",
          jsonException.getMessage());
      throw new JsonParsingException(
          "Failed to parse JSON response while starting session with AI module", jsonException);
    } catch (Exception genericException) {
      log.error(
          "Exception occurred while starting session with AI module: {}",
          genericException.getMessage());
      throw new QueryExecutionException("Error occurred while starting session", genericException);
    }
  }

  public AiModuleResponse sendQuery(String sessionId, String prompt) {
    ChatMessage chatMessageRequest = new ChatMessage(sessionId, prompt);
    log.info(
        "Received request to send query with sessionId {} and prompt : {}",
        chatMessageRequest.getSession_id(),
        chatMessageRequest.getPrompt());

    try {
      ResponseEntity<String> response =
          restTemplate.postForEntity(
              "https://784f-2601-647-6881-9160-14cf-974e-375a-fbdb.ngrok-free.app" + "/sendQuery",
              chatMessageRequest,
              String.class);

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        log.info(
            "Received query response from AI Module for sessionId: {} with body {}",
            sessionId,
            response.getBody());
        return objectMapper.readValue(response.getBody(), AiModuleResponse.class);
      } else {
        log.error(
            "Failed to send query to AI Module. Status: {}, Response Body: {}",
            response.getStatusCode(),
            response.getBody());
        throw new QueryExecutionException(
            "Error while sending query to Python module. Status: " + response.getStatusCode());
      }
    } catch (JsonProcessingException jsonException) {
      log.error(
          "JSON parsing failed while receiving response from AI Module for sendQuery: {}",
          jsonException.getMessage());
      throw new JsonParsingException(
          "Failed to parse JSON response while querying the AI module", jsonException);
    } catch (Exception genericException) {
      log.error(
          "Exception occurred while communicating query with AI module: {}",
          genericException.getMessage());
      throw new QueryExecutionException("Error occurred while sending query", genericException);
    }
  }
}
