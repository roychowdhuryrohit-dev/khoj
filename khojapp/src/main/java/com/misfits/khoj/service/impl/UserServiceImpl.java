package com.misfits.khoj.service.impl;

import com.misfits.khoj.exceptions.userexceptions.MissingUserAttributeException;
import com.misfits.khoj.exceptions.userexceptions.UserNotAuthenticatedException;
import com.misfits.khoj.exceptions.userexceptions.UserProfileException;
import com.misfits.khoj.model.UserProfile;
import com.misfits.khoj.service.UserService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  public String getCurrentJwtUserId() {
    Jwt jwt = getJwtFromAuthentication();
    if (jwt == null || !jwt.hasClaim("sub")) {
      throw new UserProfileException("User ID not found in token.");
    }
    return jwt.getClaim("sub");
  }

  public UserProfile getUserProfile(OAuth2User principal) {
    try {

      // Check if the user is authenticated
      if (principal == null) {
        throw new UserNotAuthenticatedException("User is not authenticated.");
      }

      // Retrieve and validate required attributes
      Map<String, Object> attributes = principal.getAttributes();
      validateAttribute(attributes, "sub", "User ID (sub) is missing or blank.");
      validateAttribute(attributes, "email", "Email is missing or blank.");
      validateAttribute(attributes, "name", "Name is missing or blank.");

      // Create and log the UserProfile
      UserProfile userProfile = new UserProfile(attributes);
      log.info("User profile retrieved: {}", userProfile);

      return userProfile;

    } catch (UserProfileException ex) {
      log.error("Error retrieving user profile: {}", ex.getMessage());
      throw new UserProfileException("Failed retrieving user profile details .", ex);

    } catch (Exception ex) {
      // Catch any unexpected exceptions
      log.error("An unexpected error occurred while retrieving user profile", ex);
      throw new UserProfileException("An unexpected error occurred.", ex);
    }
  }

  private void validateAttribute(
      Map<String, Object> attributes, String attributeName, String errorMessage) {
    Object attributeValue = attributes.get(attributeName);
    if (!(attributeValue instanceof String) || ((String) attributeValue).isBlank()) {
      log.error(errorMessage);
      throw new MissingUserAttributeException(errorMessage);
    }
  }

  private Jwt getJwtFromAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new UserNotAuthenticatedException("No authentication found in security context.");
    }

    if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
      throw new UserNotAuthenticatedException("Invalid token: Principal is not a JWT.");
    }

    return jwt;
  }
}
