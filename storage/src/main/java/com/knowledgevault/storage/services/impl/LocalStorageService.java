package com.knowledgevault.storage.services.impl;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.services.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Profile("local")
@Service
public class LocalStorageService implements StorageService {
    private StorageConfiguration configuration;

    public LocalStorageService(StorageConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String store(MultipartFile file) {
        return null;
    }
}
