package com.knowledgevault.storage.validation;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.exceptions.StorageException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FileValidator {

    private final StorageConfiguration configuration;
    private final Tika tika = new Tika();

    public FileValidator(StorageConfiguration configuration) {
        this.configuration = configuration;
    }

    public void validate(MultipartFile file) {
        validateBasicProperties(file);

        String detectedContentType = detectContentType(file);

        if (!configuration.getAllowedContentTypes()
                .contains(detectedContentType)) {
            throw new StorageException(
                    "Unsupported file type: " + detectedContentType
            );
        }
    }

    private void validateBasicProperties(MultipartFile file) {
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

        String cleanFilename = StringUtils.cleanPath(originalFilename);

        if (cleanFilename.contains("..")) {
            throw new StorageException("Invalid file name");
        }
    }

    private String detectContentType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.detect(
                    inputStream,
                    file.getOriginalFilename()
            );
        } catch (IOException exception) {
            throw new StorageException(
                    "Could not inspect uploaded file",
                    exception
            );
        }
    }
}