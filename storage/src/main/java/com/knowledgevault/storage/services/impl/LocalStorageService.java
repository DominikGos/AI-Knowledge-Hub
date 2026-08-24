package com.knowledgevault.storage.services.impl;

import com.knowledgevault.storage.configs.StorageConfiguration;
import com.knowledgevault.storage.entities.ProcessingStatus;
import com.knowledgevault.storage.entities.StoredFile;
import com.knowledgevault.storage.exceptions.FileNotFoundException;
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
import java.util.*;

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
        FileValidator.ValidatedFile validatedFile = fileValidator.validate(file);

        return storePhysicalValidatedFile(file, validatedFile);
    }

    @Override
    public List<StoredFile> storeAll(List<MultipartFile> files) {
        fileValidator.validateMultipartFileCollection(files);

        List<StoredFile> storedFiles = new ArrayList<>();

        for (MultipartFile file : files) {

            FileValidator.ValidatedFile validatedFile = fileValidator.validate(file);

            StoredFile storedFile = storePhysicalValidatedFile(file, validatedFile);

            storedFiles.add(storedFile);
        }

        return fileRepository.saveAll(storedFiles);
    }

    @Override
    public StoredFile delete(String storageKey) {
        StoredFile fileEntity = fileRepository
                .findByStorageKey(storageKey)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        deletePhysicalFile(fileEntity);

        fileRepository.delete(fileEntity);

        return fileEntity;
    }

    @Override
    public List<StoredFile> deleteAll(List<String> storageKeys) {
        fileValidator.validateStorageKeysCollection(storageKeys);

        List<StoredFile> fileEntities = fileRepository.findAllByStorageKeyIn(storageKeys);

        if(fileEntities == null || fileEntities.isEmpty()) {
            throw new FileNotFoundException("Couldn't find any of the files");
        }

        fileEntities.forEach(this::deletePhysicalFile);

        fileRepository.deleteAll(fileEntities);
        
        return fileEntities;
    }

    private StoredFile storePhysicalValidatedFile(MultipartFile file, FileValidator.ValidatedFile validatedFile) {
        String storageKey = UUID.randomUUID() + validatedFile.extension();
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
            throw new StorageException("Could not store file: " + file.getOriginalFilename());
        }
    }

    private void deletePhysicalFile(StoredFile file) {
        Path path = resolveStoragePath(file.getStorageKey());

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new StorageException("Could not delete file: " + file.getStorageKey());
        }
    }

    private Path resolveStoragePath(String storageKey) {
        Path targetLocation = rootLocation
                .resolve(storageKey)
                .normalize();

        if (!targetLocation.startsWith(rootLocation)) {
            throw new StorageException("Invalid storage path");
        }

        return targetLocation;
    }

    private void initializeStorageDirectory() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException exception) {
            throw new StorageException("Could not create storage directory: " + rootLocation);
        }
    }
}
