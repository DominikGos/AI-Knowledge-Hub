package com.knowledgevault.storage.validation;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.entities.StoredFile;
import com.knowledgevault.storage.exceptions.FileValidationException;
import com.knowledgevault.storage.exceptions.StorageException;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class FileValidator {

    private final StorageConfiguration configuration;
    private final Tika tika = new Tika();

    public FileValidator(StorageConfiguration configuration) {
        this.configuration = configuration;
    }

    public ValidatedFile validate(MultipartFile file) {
        validateBasicProperties(file);

        String detectedContentType = detectContentType(file);

        if (!configuration.getAllowedContentTypes()
                .contains(detectedContentType)) {
            throw new FileValidationException("Unsupported file type: " + detectedContentType);
        }

        return new ValidatedFile(
                detectedContentType,
                extensionFor(detectedContentType)
        );
    }

    private void validateBasicProperties(MultipartFile file) {
        if (file == null) {
            throw new FileValidationException("File is required");
        }

        if (file.isEmpty()) {
            throw new FileValidationException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new FileValidationException("File name is missing");
        }

        String cleanFilename = StringUtils.cleanPath(originalFilename);

        if (cleanFilename.contains("..")) {
            throw new FileValidationException("Invalid file name");
        }
    }

    private String detectContentType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.detect(
                    inputStream,
                    file.getOriginalFilename()
            );
        } catch (IOException exception) {
            throw new FileValidationException("Could not inspect uploaded file");
        }
    }

    private String extensionFor(String contentType) {
        try {
            return MimeTypes.getDefaultMimeTypes()
                    .forName(contentType)
                    .getExtension();
        } catch (MimeTypeException exception) {
            throw new FileValidationException(
                    "Could not determine extension for file type: " + contentType
            );
        }
    }

    public void validateMultipartFileCollection(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new FileValidationException("At least one file must be provided");
        }

        if (files.size()
                > configuration.getMaxFilesPerRequest()) {
            throw new FileValidationException(
                    "Too many files. Maximum allowed: " + configuration.getMaxFilesPerRequest()
            );
        }

        files.forEach(this::validate);
    }

    public void validateStorageKeysCollection(List<String> storageKeys) {
        if (storageKeys == null || storageKeys.isEmpty()) {
            throw new FileValidationException(
                    "At least one storage key must be provided"
            );
        }

        if (storageKeys.stream().anyMatch(
                key -> key == null || key.isBlank()
        )) {
            throw new FileValidationException(
                    "Storage keys cannot be empty"
            );
        }
    }

    public record ValidatedFile(
            String contentType,
            String extension
    ) {
    }
}
