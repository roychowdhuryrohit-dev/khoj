package com.misfits.khoj.controller;

import com.misfits.khoj.model.chat.ChatMessage;
import com.misfits.khoj.model.chat.SessionStore;
import com.misfits.khoj.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

  private final ChatService chatService;

  private final SessionStore sessionStore;

  public WebSocketController(ChatService chatService, SessionStore sessionStore) {
    this.chatService = chatService;
    this.sessionStore = sessionStore;
  }

  @MessageMapping("/sendMessage")
  @SendTo("/topic/messages")
  public String handleMessage(ChatMessage message, @AuthenticationPrincipal OAuth2User principal) {
    // Step 1: Get userId from the authenticated principal
    String userId =
        principal.getAttribute("sub"); // Or use your custom userService.getUserId(principal)

    // Step 2: Retrieve the stored sessionId for this user
    String storedSessionId = sessionStore.getSessionId(userId);

    // Step 3: Validate the sessionId
    if (!message.getSession_id().equals(storedSessionId)) {
      throw new IllegalArgumentException("Invalid sessionId for user: " + userId);
    }

    // Step 4: Process the query via the ChatService
    return chatService.sendQuery(message.getSession_id(), message.getPrompt());
  }
}
