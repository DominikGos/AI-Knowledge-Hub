package com.knowledgevault.storage.services.impl;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.dto.UploadedFileResponse;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Profile("local")
@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;
    private final FileValidator fileValidator;
    private final StorageConfiguration configuration;

    public LocalStorageService(
            StorageConfiguration configuration,
            FileValidator fileValidator
    ) {
        this.configuration = configuration;
        this.fileValidator = fileValidator;

        this.rootLocation = Path.of(configuration.getLocation())
                .toAbsolutePath()
                .normalize();

        initializeStorageDirectory();
    }

    @Override
    public UploadedFileResponse store(MultipartFile file) {
        fileValidator.validate(file);

        return storeValidatedFile(file);
    }

    @Override
    public List<UploadedFileResponse> storeAll(
            List<MultipartFile> files
    ) {
        validateFileCollection(files);

        /*
         * Validate all files before saving any of them.
         * This prevents partial uploads caused by validation errors.
         */
        files.forEach(fileValidator::validate);

        return files.stream()
                .map(this::storeValidatedFile)
                .toList();
    }

    private UploadedFileResponse storeValidatedFile(
            MultipartFile file
    ) {
        String extension = extractExtension(
                file.getOriginalFilename()
        );

        String storageKey = UUID.randomUUID() + extension;
        Path targetLocation = resolveStoragePath(storageKey);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return UploadedFileResponse.builder()
                    .originalFilename(file.getOriginalFilename())
                    .storageKey(storageKey)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .build();

        } catch (IOException exception) {
            throw new StorageException(
                    "Could not store file: "
                            + file.getOriginalFilename(),
                    exception
            );
        }
    }

    private void validateFileCollection(
            List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            throw new StorageException(
                    "At least one file must be provided"
            );
        }

        if (files.size()
                > configuration.getMaxFilesPerRequest()) {
            throw new StorageException(
                    "Too many files. Maximum allowed: "
                            + configuration.getMaxFilesPerRequest()
            );
        }
    }

    private Path resolveStoragePath(String storageKey) {
        Path targetLocation = rootLocation
                .resolve(storageKey)
                .normalize();

        if (!targetLocation.startsWith(rootLocation)) {
            throw new StorageException(
                    "Invalid storage path"
            );
        }

        return targetLocation;
    }

    private void initializeStorageDirectory() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException exception) {
            throw new StorageException(
                    "Could not create storage directory: "
                            + rootLocation,
                    exception
            );
        }
    }

    private String extractExtension(
            String originalFilename
    ) {
        String cleanFilename =
                StringUtils.cleanPath(originalFilename);

        int extensionIndex =
                cleanFilename.lastIndexOf('.');

        if (extensionIndex < 0
                || extensionIndex
                == cleanFilename.length() - 1) {
            return "";
        }

        String extension = cleanFilename
                .substring(extensionIndex)
                .toLowerCase(Locale.ROOT);

        return extension.length() <= 20
                ? extension
                : "";
    }
}
