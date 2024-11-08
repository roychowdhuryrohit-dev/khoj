package com.misfits.khoj.utils;

import com.misfits.khoj.exceptions.fileexceptions.FileStandardizationException;
import org.springframework.stereotype.Service;

@Service
public class KhojUtils {

  public static String standardizeFileName(String fileName) {
    if (fileName == null) {
      throw new FileStandardizationException("File name cannot be null");
    }
    return fileName.replaceAll("\\s+", "_"); // Replace spaces with underscores
  }
}
