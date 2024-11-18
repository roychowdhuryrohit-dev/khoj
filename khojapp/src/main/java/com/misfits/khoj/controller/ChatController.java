package com.misfits.khoj.controller;

import com.misfits.khoj.model.chat.ChatSessionRequest;
import com.misfits.khoj.service.ChatService;
import com.misfits.khoj.service.S3FileService;
import com.misfits.khoj.service.UserService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

  private final S3FileService s3FileService;

  private final UserService userService;

  private final ChatService chatService;

  public ChatController(
      UserService userService, S3FileService s3FileService, ChatService chatService) {
    this.userService = userService;
    this.s3FileService = s3FileService;
    this.chatService = chatService;
  }

  @PostMapping("/startChatSession")
  public ResponseEntity<String> startChatSession(
      @RequestBody ChatSessionRequest request, @AuthenticationPrincipal OAuth2User principal) {

    String sessionId = UUID.randomUUID().toString();
    List<String> fileUrls =
        s3FileService.generatePresignedUrlsForUserFiles(
            request.getFilenames(), userService.getUserId(principal));
    String response = chatService.startSession(sessionId, fileUrls);
    return ResponseEntity.ok(response);
  }
}
