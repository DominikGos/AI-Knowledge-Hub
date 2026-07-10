package com.knowledgevault.storage.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    public String store(MultipartFile file);
}
