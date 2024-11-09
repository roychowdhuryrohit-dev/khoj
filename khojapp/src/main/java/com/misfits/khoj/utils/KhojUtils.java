package com.misfits.khoj.utils;

import static com.misfits.khoj.constants.ApplicationConstants.SPACE_REGEX;
import static com.misfits.khoj.constants.ApplicationConstants.UNDERSCORE;

import com.misfits.khoj.exceptions.fileexceptions.FileStandardizationException;
import com.misfits.khoj.exceptions.userexceptions.MissingUserAttributeException;
import com.misfits.khoj.exceptions.userexceptions.UserProfileException;
import org.springframework.stereotype.Service;

@Service
public class KhojUtils {

  public static String standardizeFileName(String fileName) {
    try {
      if (fileName == null) {
        throw new FileStandardizationException("File name cannot be null");
      }
      return fileName.replaceAll(SPACE_REGEX, UNDERSCORE); // Replace spaces with underscores
    } catch (FileStandardizationException e) {
      throw new FileStandardizationException("File name cannot be null", e);
    } // Replace spaces with underscores
  }

  public static String validateUserIdNotNull(String userId) {
    try {
      if (userId == null) {
        throw new MissingUserAttributeException("User ID supplied cannot be null");
      }
      return userId;
    } catch (UserProfileException e) {
      throw new MissingUserAttributeException("User ID supplied cannot be null", e);
    }
  }
}
