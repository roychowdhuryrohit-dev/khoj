package com.misfits.khoj.controller;

import com.misfits.khoj.model.chat.ChatMessage;
import com.misfits.khoj.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

  private final ChatService chatService;

  public WebSocketController(ChatService chatService) {
    this.chatService = chatService;
  }

  @MessageMapping("/sendMessage")
  @SendTo("/topic/messages")
  public String handleMessage(ChatMessage message) {
    return chatService.sendQuery(message.getSessionId(), message.getPrompt());
  }
}
