package com.knowledgevault.storage.validation;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.exceptions.StorageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    private final StorageConfiguration configuration;

    public FileValidator(StorageConfiguration configuration) {
        this.configuration = configuration;
    }

    public void validate(MultipartFile file) {
        if (file == null) {
            throw new StorageException("File is required");
        }

        if (file.isEmpty()) {
            throw new StorageException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new StorageException("File name is missing");
        }

        String contentType = file.getContentType();

        if (contentType == null || contentType.isBlank()) {
            throw new StorageException("File content type is missing");
        }

        if (!configuration.getAllowedContentTypes().contains(contentType)) {
            throw new StorageException(
                    "Unsupported content type: " + contentType
            );
        }
    }
}