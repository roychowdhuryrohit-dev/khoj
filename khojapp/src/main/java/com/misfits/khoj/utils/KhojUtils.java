package com.misfits.khoj.utils;

import static com.misfits.khoj.constants.ApplicationConstants.SPACE_REGEX;
import static com.misfits.khoj.constants.ApplicationConstants.UNDERSCORE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.misfits.khoj.exceptions.file.FileStandardizationException;
import com.misfits.khoj.exceptions.persitence.UserDataSerializationException;
import com.misfits.khoj.exceptions.user.MissingUserAttributeException;
import com.misfits.khoj.exceptions.user.UserProfileException;
import com.misfits.khoj.model.persistence.UserProfileDetails;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Service
@Slf4j
public class KhojUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String standardizeFileName(String fileName) {
    try {
      if (fileName == null) {
        throw new FileStandardizationException("File name cannot be null");
      }
      return fileName.replaceAll(SPACE_REGEX, UNDERSCORE); // Replace spaces with underscores
    } catch (FileStandardizationException e) {
      throw new FileStandardizationException("Exception occurred while standardizing filename", e);
    }
  }

  public static String validateUserIdNotNull(String userId) {
    try {
      log.info("Validating userID");
      if (userId == null) {
        throw new MissingUserAttributeException("User ID supplied cannot be null");
      }
      return userId;
    } catch (UserProfileException e) {
      throw new MissingUserAttributeException("User ID supplied cannot be null", e);
    }
  }

  public static Map<String, AttributeValue> convertUserProfileToAttributeValueMap(
      UserProfileDetails userProfileDetails) {
    try {
      Map<String, Object> userKeyValueMap =
          objectMapper.convertValue(userProfileDetails, new TypeReference<>() {});
      Map<String, AttributeValue> attributeValueMap = new HashMap<>();

      userKeyValueMap.forEach((key, value) -> attributeValueMap.put(key, toAttributeValue(value)));
      return attributeValueMap;
    } catch (Exception e) {
      throw new UserDataSerializationException(
          "Error converting UserProfile to AttributeValue map", e);
    }
  }

  private static AttributeValue toAttributeValue(Object value) {
    if (value == null) return AttributeValue.builder().nul(true).build();
    if (value instanceof String s) return AttributeValue.builder().s(s).build();
    if (value instanceof Number n) return AttributeValue.builder().n(n.toString()).build();
    if (value instanceof Boolean b) return AttributeValue.builder().bool(b).build();
    if (value instanceof Map<?, ?> map) {
      Map<String, AttributeValue> nestedMap = new HashMap<>();
      map.forEach((k, v) -> nestedMap.put(k.toString(), toAttributeValue(v)));
      return AttributeValue.builder().m(nestedMap).build();
    }
    if (value instanceof List<?> list) {
      List<AttributeValue> attributeValues =
          list.stream().map(KhojUtils::toAttributeValue).toList();
      return AttributeValue.builder().l(attributeValues).build();
    }
    return AttributeValue.builder().s(value.toString()).build();
  }
}
