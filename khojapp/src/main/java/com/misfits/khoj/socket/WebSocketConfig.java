package com.misfits.khoj.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final WebSocketAuthInterceptor authInterceptor;

  public WebSocketConfig(WebSocketAuthInterceptor authInterceptor) {
    this.authInterceptor = authInterceptor;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic"); // Server-to-client messages
    config.setApplicationDestinationPrefixes("/app"); // Client-to-server messages
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/chat")
        .setAllowedOrigins("http://localhost:8080", "http://localhost:8081") // Allow both origins
        .addInterceptors(authInterceptor)
        .withSockJS(); // Enable fallback options for browsers without WebSocket support
  }
}
