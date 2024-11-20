package com.misfits.khoj.socket;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes)
      throws Exception {
    if (request instanceof ServletServerHttpRequest servletRequest) {
      HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

      // Retrieve the authenticated user from the SecurityContext
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getPrincipal() instanceof OAuth2User principal) {
        attributes.put(
            "principal", principal); // Attach authenticated principal to WebSocket attributes
        return true;
      }
    }
    return false; // Reject the handshake if authentication fails
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest request,
      ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception ex) {
    // No post-handshake logic needed
  }
}
