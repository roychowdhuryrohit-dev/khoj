package com.misfits.khoj.controller;

import com.misfits.khoj.exceptions.chat.QueryExecutionException;
import com.misfits.khoj.exceptions.chat.SessionStartException;
import com.misfits.khoj.exceptions.file.FileListingException;
import com.misfits.khoj.exceptions.file.s3.PresignedUrlGenerationException;
import com.misfits.khoj.exceptions.file.s3.S3PresignedUrlException;
import com.misfits.khoj.exceptions.user.UserNotAuthenticatedException;
import com.misfits.khoj.model.chat.AiModuleResponse;
import com.misfits.khoj.model.chat.ChatSessionRequest;
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

  public ChatController(
      UserService userService, S3FileService s3FileService, ChatService chatService) {
    this.userService = userService;
    this.s3FileService = s3FileService;
    this.chatService = chatService;
  }

  @PostMapping("/startChatSession")
  public ResponseEntity<AiModuleResponse> startChatSession(
      @RequestBody ChatSessionRequest request, @AuthenticationPrincipal OAuth2User principal) {
    try {
      log.info("Received request to start chat session: {}", request.toString());

      if (principal == null) {
        log.error("User authentication failed: Principal is null.");
        throw new UserNotAuthenticatedException("User is not authenticated.");
      }

      String sessionId = userService.getUserId(principal);

      List<String> fileUrls =
          s3FileService.generatePresignedUrlsForUserFiles(request.getFilenames(), sessionId);
      log.info("Generated preSigned URLs for files: {}", fileUrls);

      AiModuleResponse aiModuleResponse = chatService.startSession(sessionId, fileUrls);
      log.info("Successfully started chat session with sessionId: {}", sessionId);

      return ResponseEntity.ok(aiModuleResponse);

    } catch (UserNotAuthenticatedException ex) {
      log.error("UserNotAuthenticatedException occurred: {}", ex.getMessage());
      throw ex;
    } catch (FileListingException | PresignedUrlGenerationException ex) {
      log.error("Error occurred while generating preSigned URLs: {}", ex.getMessage());
      throw new S3PresignedUrlException("Failed to generate preSigned URLs for user files.", ex);
    } catch (SessionStartException ex) {
      log.error(
          "SessionStartException occurred while starting the chat session: {}", ex.getMessage());
      throw ex;
    } catch (Exception ex) {
      log.error("Error occurred while starting chat session: {}", ex.getMessage(), ex);
      throw new QueryExecutionException("Exception occurred while starting chat session.", ex);
    }
  }
}
