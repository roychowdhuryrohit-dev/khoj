package com.misfits.khoj.service;

import com.misfits.khoj.model.file.FileUploadResponse;
import com.misfits.khoj.model.file.ListUserFilesResponse;
import com.misfits.khoj.model.file.MultipleFileUploadResponse;
import com.misfits.khoj.model.file.PreSignedUrlResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface S3FileService {

  FileUploadResponse uploadFile(MultipartFile file, String userId);

  MultipleFileUploadResponse uploadFiles(List<MultipartFile> files, String userId);

  ListUserFilesResponse listUserFiles(String userId);

  PreSignedUrlResponse getPresignedUrlForFile(String fileName, String userId);
}
