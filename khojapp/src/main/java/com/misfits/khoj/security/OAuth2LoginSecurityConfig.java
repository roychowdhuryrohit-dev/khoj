package com.misfits.khoj.security;

import static com.misfits.khoj.constants.ApplicationConstants.LOGIN_PAGE;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class OAuth2LoginSecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        auth -> auth.anyRequest().authenticated() // Secure all endpoints
    )
        .oauth2Login(oauth2 -> oauth2.loginPage(LOGIN_PAGE))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
        .csrf(csrf -> csrf.disable()) // Disable CSRF for WebSocket compatibility
        .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Enable CORS

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:8080"); // Allow origin 8080
    configuration.addAllowedOrigin("http://localhost:8081"); // Allow origin 8081
    configuration.addAllowedMethod("*"); // Allow all HTTP methods
    configuration.addAllowedHeader("*"); // Allow all headers
    configuration.setAllowCredentials(true); // Allow credentials

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // Apply globally
    return source;
  }
}