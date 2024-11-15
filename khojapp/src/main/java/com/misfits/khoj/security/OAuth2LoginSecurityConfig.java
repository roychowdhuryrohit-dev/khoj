package com.misfits.khoj.security;

import static com.misfits.khoj.constants.ApplicationConstants.LOGIN_PAGE;
import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class OAuth2LoginSecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
        auth -> auth.anyRequest().authenticated() // Secure all other endpoints
    )
        .oauth2Login(oauth2 -> oauth2.loginPage(LOGIN_PAGE))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
        .csrf(csrf -> csrf.disable()) // Disable CSRF
        .cors(cors -> cors.disable()); // Disable CORS

    return http.build();
  }
}
