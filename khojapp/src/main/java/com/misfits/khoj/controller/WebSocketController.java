package com.misfits.khoj.controller;

import static com.misfits.khoj.utils.KhojUtils.validateUserIdNotNull;

import com.misfits.khoj.constants.ApplicationConstants;
import com.misfits.khoj.exceptions.chat.QueryExecutionException;
import com.misfits.khoj.exceptions.user.MissingUserAttributeException;
import com.misfits.khoj.exceptions.user.UserNotAuthenticatedException;
import com.misfits.khoj.model.chat.AiModuleResponse;
import com.misfits.khoj.model.chat.ChatMessage;
import com.misfits.khoj.service.ChatService;
import java.security.Principal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WebSocketController {

  private final ChatService chatService;

  public WebSocketController(ChatService chatService) {
    this.chatService = chatService;
  }

  @MessageMapping("/sendMessage")
  @SendTo("/topic/messages")
  public AiModuleResponse handleMessage(ChatMessage chatMessage, Principal principal) {
    try {

      log.info("Received chatMessage for processing from AI module");

      if (principal == null) {
        log.error("User authentication failed: Principal is null.");
        throw new UserNotAuthenticatedException("User is not authenticated.");
      }

      Authentication authentication = (Authentication) principal;
      OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
      String userId = oauth2User.getAttribute(ApplicationConstants.SUB);
      chatMessage.setSession_id(userId);
      validateUserIdNotNull(userId);

      AiModuleResponse response = chatService.sendQuery(userId, chatMessage.getPrompt());
      log.info(
          "Successfully processed query for userId: {}, sending response {}",
          userId,
          response.toString());
      return response;

    } catch (UserNotAuthenticatedException ex) {
      log.error(
          "UserNotAuthenticatedException occurred while forwarding query to AI Module: {}",
          ex.getMessage());
      throw ex;
    } catch (MissingUserAttributeException ex) {
      log.error(
          "MissingUserAttributeException occurred while forwarding query to AI Module: {}",
          ex.getMessage());
      throw ex;
    } catch (Exception ex) {
      log.error("Error occurred while processing chatMessage: {}", ex.getMessage(), ex);
      throw new QueryExecutionException("Error while processing chatMessage.", ex);
    }
  }
}
