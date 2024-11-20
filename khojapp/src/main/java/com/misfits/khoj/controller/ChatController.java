package com.misfits.khoj.controller;

import com.misfits.khoj.model.chat.AiModuleResponse;
import com.misfits.khoj.model.chat.ChatSessionRequest;
import com.misfits.khoj.model.chat.SessionStore;
import com.misfits.khoj.service.ChatService;
import com.misfits.khoj.service.S3FileService;
import com.misfits.khoj.service.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ChatController {

  private final S3FileService s3FileService;

  private final UserService userService;

  private final ChatService chatService;

  private final SessionStore sessionStore;

  public ChatController(
      UserService userService,
      S3FileService s3FileService,
      ChatService chatService,
      SessionStore sessionStore) {
    this.userService = userService;
    this.s3FileService = s3FileService;
    this.chatService = chatService;
    this.sessionStore = sessionStore;
  }

  @PostMapping("/startChatSession")
  public ResponseEntity<AiModuleResponse> startChatSession(
      @RequestBody ChatSessionRequest request, @AuthenticationPrincipal OAuth2User principal) {

    String userId = userService.getUserId(principal);
    String sessionId = userService.getUserId(principal);
    sessionStore.saveSession(userId, sessionId);
    log.info("Session ID: {} , UserId {}, request: {} ", sessionId, userId, request.toString());
    List<String> fileUrls =
        s3FileService.generatePresignedUrlsForUserFiles(
            request.getFilenames(), userService.getUserId(principal));
    String response = chatService.startSession(sessionId, fileUrls);
    AiModuleResponse aiModuleResponse = new AiModuleResponse(response);
    return ResponseEntity.ok(aiModuleResponse);
  }
}
