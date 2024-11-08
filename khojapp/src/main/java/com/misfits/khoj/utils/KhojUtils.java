package com.misfits.khoj.utils;

import static com.misfits.khoj.constants.ApplicationConstants.SPACE_REGEX;
import static com.misfits.khoj.constants.ApplicationConstants.UNDERSCORE;

import com.misfits.khoj.exceptions.fileexceptions.FileStandardizationException;
import org.springframework.stereotype.Service;

@Service
public class KhojUtils {

  public static String standardizeFileName(String fileName) {
    if (fileName == null) {
      throw new FileStandardizationException("File name cannot be null");
    }
    return fileName.replaceAll(SPACE_REGEX, UNDERSCORE); // Replace spaces with underscores
  }
}
