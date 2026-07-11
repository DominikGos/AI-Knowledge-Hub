package com.knowledgevault.storage.services.impl;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.exceptions.StorageException;
import com.knowledgevault.storage.services.StorageService;
import com.knowledgevault.storage.validation.FileValidator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Profile("local")
@Service
public class LocalStorageService implements StorageService {
    private final Path rootLocation;
    private final FileValidator fileValidator;

    public LocalStorageService(
            StorageConfiguration configuration,
            FileValidator fileValidator
    ) {
        this.rootLocation = Path.of(configuration.getLocation())
                .toAbsolutePath()
                .normalize();

        this.fileValidator = fileValidator;

        initializeStorageDirectory();
    }

    @Override
    public String store(MultipartFile file) {
        fileValidator.validate(file);

        String extension = extractExtension(file.getOriginalFilename());
        String storageKey = UUID.randomUUID() + extension;

        Path targetLocation = rootLocation
                .resolve(storageKey)
                .normalize();

        /*
         * Prevent a generated or provided path from escaping
         * the configured upload directory.
         */
        if (!targetLocation.startsWith(rootLocation)) {
            throw new StorageException("Invalid storage path");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return storageKey;
        } catch (IOException exception) {
            throw new StorageException(
                    "Could not store file: " + file.getOriginalFilename(),
                    exception
            );
        }
    }

    private void initializeStorageDirectory() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException exception) {
            throw new StorageException(
                    "Could not create storage directory: " + rootLocation,
                    exception
            );
        }
    }

    private String extractExtension(String originalFilename) {
        String cleanFilename = StringUtils.cleanPath(originalFilename);

        int extensionIndex = cleanFilename.lastIndexOf('.');

        if (extensionIndex < 0 ||
                extensionIndex == cleanFilename.length() - 1) {
            return "";
        }

        String extension = cleanFilename
                .substring(extensionIndex)
                .toLowerCase(Locale.ROOT);

        return extension.length() <= 20 ? extension : "";
    }
}
