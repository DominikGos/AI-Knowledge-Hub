package com.knowledgevault.storage.services.impl;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.entities.ProcessingStatus;
import com.knowledgevault.storage.entities.StoredFile;
import com.knowledgevault.storage.exceptions.StorageException;
import com.knowledgevault.storage.repositories.FileRepository;
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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Profile("local")
@Service
public class LocalStorageService implements StorageService {

    private final Path rootLocation;
    private final FileValidator fileValidator;
    private final StorageConfiguration configuration;
    private final FileRepository fileRepository;
    private final StorageConfiguration storageConfiguration;

    public LocalStorageService(
            StorageConfiguration configuration,
            FileValidator fileValidator,
            FileRepository fileRepository,
            StorageConfiguration storageConfiguration) {
        this.configuration = configuration;
        this.fileValidator = fileValidator;
        this.fileRepository = fileRepository;
        this.storageConfiguration = storageConfiguration;

        this.rootLocation = Path.of(configuration.getLocation())
                .toAbsolutePath()
                .normalize();

        initializeStorageDirectory();
    }

    @Override
    public StoredFile store(MultipartFile file) {
        fileValidator.validate(file);

        return storeValidatedFile(file);
    }

    @Override
    public List<StoredFile> storeAll(List<MultipartFile> files) {
        validateFileCollection(files);

        files.forEach(fileValidator::validate);

         List<StoredFile> validatedFiles = files.stream()
                .map(this::storeValidatedFile)
                .toList();

         return fileRepository.saveAll(validatedFiles);
    }

    @Override
    public StoredFile delete(String storageKey) {
        StoredFile fileEntity = fileRepository
                .findByStorageKey(storageKey)
                .orElseThrow(() -> new NullPointerException("File not found"));

        fileRepository.delete(fileEntity);

        return fileEntity;
    }

    private StoredFile storeValidatedFile(MultipartFile file) {
        String extension = extractExtension(file.getOriginalFilename());
        String storageKey = UUID.randomUUID() + extension;
        Path targetLocation = resolveStoragePath(storageKey);
        StoredFile fileEntity;

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

             fileEntity = StoredFile.builder()
                    .processingStatus(ProcessingStatus.PENDING)
                    .originalFilename(file.getOriginalFilename())
                    .storageKey(storageKey)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            return fileEntity;
        } catch (IOException exception) {
            throw new StorageException(
                    "Could not store file: " + file.getOriginalFilename(),
                    exception
            );
        }
    }

    private void deletePhysicalFile(StoredFile file) {
        Path path = resolveStoragePath(file.getStorageKey());

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new StorageException("Could not delete file: " + file.getStorageKey(), e);
        }
    }

    private void validateFileCollection(List<MultipartFile> files) {
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

    private String extractExtension(String originalFilename) {
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
