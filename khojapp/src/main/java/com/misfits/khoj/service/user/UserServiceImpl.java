package com.misfits.khoj.service.user;

import static com.misfits.khoj.constants.ApplicationConstants.*;

import com.misfits.khoj.exceptions.user.MissingUserAttributeException;
import com.misfits.khoj.exceptions.user.UserNotAuthenticatedException;
import com.misfits.khoj.exceptions.user.UserProfileException;
import com.misfits.khoj.model.persistence.UserProfileDetails;
import com.misfits.khoj.model.user.UserProfile;
import com.misfits.khoj.service.UserService;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

  final CognitoIdentityProviderClient cognitoIdentityProviderClient;

  public UserServiceImpl(CognitoIdentityProviderClient cognitoIdentityProviderClient) {
    this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
  }

  @Override
  public String getUserId(OAuth2User principal) {
    if (principal == null) {
      throw new UserNotAuthenticatedException(
          "User is not authenticated, Principal is null in getUserId.");
    }

    return (String)
        Optional.ofNullable(principal.getAttribute(SUB))
            .orElseThrow(
                () ->
                    new MissingUserAttributeException(
                        "User ID (sub) attribute is missing in the OAuth2User principal."));
  }

  @Override
  public UserProfile getUserProfile(OAuth2User principal) {
    try {

      // Check if the user is authenticated
      if (principal == null) {
        throw new UserNotAuthenticatedException(
            "User is not authenticated, Principal is null in getUserProfile.");
      }

      // Retrieve and validate required attributes
      Map<String, Object> attributes = principal.getAttributes();
      validateAttribute(attributes, SUB, "User ID (sub) is missing or blank.");
      validateAttribute(attributes, EMAIL, "Email is missing or blank.");
      validateAttribute(attributes, NAME, "Name is missing or blank.");

      // Create and log the UserProfile
      UserProfile userProfile = new UserProfile(attributes);
      log.info("User profile retrieved: {}", userProfile);
      return userProfile;

    } catch (UserProfileException ex) {
      log.error("Error retrieving user profile: {}", ex.getMessage());
      throw new UserProfileException(
          "Failed retrieving user profile details .", String.valueOf(ex));

    } catch (Exception ex) {
      // Catch any unexpected exceptions
      log.error("An unexpected error occurred while retrieving user profile", ex);
      throw new UserProfileException("An unexpected error occurred.", String.valueOf(ex));
    }
  }

  @Override
  public UserProfileDetails fetchUserDetails(String userPoolId, String userId) {
    try {
      AdminGetUserRequest request = getAdminGetUserRequest(userPoolId, userId);
      AdminGetUserResponse response = cognitoIdentityProviderClient.adminGetUser(request);

      Map<String, String> attributesMap =
          response.userAttributes().stream()
              .collect(Collectors.toMap(AttributeType::name, AttributeType::value));

      return getUserProfileDetails(userId, attributesMap);

    } catch (CognitoIdentityProviderException e) {
      throw new UserProfileException(e, "Error retrieving user profile details from cognito");
    }
  }

  private static AdminGetUserRequest getAdminGetUserRequest(String userPoolId, String userId) {
    return AdminGetUserRequest.builder().userPoolId(userPoolId).username(userId).build();
  }

  private UserProfileDetails getUserProfileDetails(
      String userId, Map<String, String> attributesMap) {
    UserProfileDetails userProfileDetails = new UserProfileDetails();
    userProfileDetails.setUserId(userId);
    userProfileDetails.setEmail(getRequiredAttribute(EMAIL, attributesMap));
    userProfileDetails.setEmailVerified(
        Boolean.parseBoolean(getRequiredAttribute(EMAIL_VERIFIED, attributesMap)));
    userProfileDetails.setName(getRequiredAttribute(NAME, attributesMap));

    return userProfileDetails;
  }

  private String getRequiredAttribute(String attributeName, Map<String, String> attributesMap) {
    try {
      String value = attributesMap.get(attributeName);
      if (value == null || value.isEmpty()) {
        throw new MissingUserAttributeException("Missing required attribute: " + attributeName);
      }
      return value;
    } catch (Exception e) {
      log.error("Error retrieving attribute: {}", attributeName, e);
      throw new MissingUserAttributeException(
          "An error occurred while retrieving attribute: " + attributeName, e);
    }
  }

  private void validateAttribute(
      Map<String, Object> attributes, String attributeName, String errorMessage) {
    Object attributeValue = attributes.get(attributeName);
    if (!(attributeValue instanceof String s) || (s).isBlank()) {
      log.error(errorMessage);
      throw new MissingUserAttributeException(errorMessage);
    }
  }
}
