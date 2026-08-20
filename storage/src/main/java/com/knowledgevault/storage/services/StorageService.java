package com.knowledgevault.storage.services;

import com.knowledgevault.storage.dto.UploadedFileResponse;
import com.knowledgevault.storage.entities.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {
    public StoredFile store(MultipartFile file);

    public List<StoredFile> storeAll(List<MultipartFile> files);

    public StoredFile delete(String storageKey);

    public List<StoredFile> deleteAll(List<String> storageKeys);
}
