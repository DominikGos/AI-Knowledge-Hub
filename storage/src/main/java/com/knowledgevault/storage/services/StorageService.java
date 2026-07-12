package com.knowledgevault.storage.services;

import com.knowledgevault.storage.dto.UploadedFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {
    public UploadedFileResponse  store(MultipartFile file);

    List<UploadedFileResponse> storeAll(List<MultipartFile> files);
}
