package com.knowledgevault.storage.services.impl;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.dto.UploadedFileResponse;
import com.knowledgevault.storage.exceptions.StorageException;
import com.knowledgevault.storage.services.StorageService;
import com.knowledgevault.storage.validation.FileValidator;
import com.knowledgevault.storage.validation.FileValidator.ValidatedFile;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
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
        ValidatedFile validatedFile = fileValidator.validate(file);

        return storeValidatedFile(file, validatedFile).response();
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
        List<ValidatedFile> validatedFiles = files.stream()
                .map(fileValidator::validate)
                .toList();

        List<StoredFile> storedFiles = new ArrayList<>();

        try {
            for (int index = 0; index < files.size(); index++) {
                storedFiles.add(storeValidatedFile(
                        files.get(index),
                        validatedFiles.get(index)
                ));
            }

            return storedFiles.stream()
                    .map(StoredFile::response)
                    .toList();
        } catch (RuntimeException exception) {
            rollback(storedFiles, exception);
            throw exception;
        }
    }

    private StoredFile storeValidatedFile(
            MultipartFile file,
            ValidatedFile validatedFile
    ) {
        String storageKey = UUID.randomUUID()
                + validatedFile.extension();
        Path targetLocation = resolveStoragePath(storageKey);
        Path temporaryFile = null;

        try (InputStream inputStream = file.getInputStream()) {
            temporaryFile = Files.createTempFile(
                    rootLocation,
                    ".upload-",
                    ".tmp"
            );
            Files.copy(
                    inputStream,
                    temporaryFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
            moveToFinalLocation(temporaryFile, targetLocation);

            return new StoredFile(
                    targetLocation,
                    UploadedFileResponse.builder()
                            .originalFilename(file.getOriginalFilename())
                            .storageKey(storageKey)
                            .contentType(validatedFile.contentType())
                            .size(file.getSize())
                            .build()
            );

        } catch (IOException exception) {
            deleteTemporaryFile(temporaryFile, exception);
            throw new StorageException(
                    "Could not store file: "
                            + file.getOriginalFilename(),
                    exception
            );
        }
    }

    private void moveToFinalLocation(
            Path source,
            Path target
    ) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target);
        }
    }

    private void rollback(
            List<StoredFile> storedFiles,
            RuntimeException originalException
    ) {
        for (StoredFile storedFile : storedFiles) {
            try {
                Files.deleteIfExists(storedFile.path());
            } catch (IOException rollbackException) {
                originalException.addSuppressed(rollbackException);
            }
        }
    }

    private void deleteTemporaryFile(
            Path temporaryFile,
            IOException originalException
    ) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException cleanupException) {
            originalException.addSuppressed(cleanupException);
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

    private record StoredFile(
            Path path,
            UploadedFileResponse response
    ) {
    }
}
