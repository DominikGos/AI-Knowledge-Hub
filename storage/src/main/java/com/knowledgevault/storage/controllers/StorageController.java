package com.knowledgevault.storage.controllers;

import com.knowledgevault.storage.dto.DeletedFileResponse;
import com.knowledgevault.storage.dto.UploadedFileResponse;
import com.knowledgevault.storage.entities.StoredFile;
import com.knowledgevault.storage.mappers.FileMapper;
import com.knowledgevault.storage.services.StorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;
    private final FileMapper fileMapper;

    public StorageController(
        StorageService storageService,
        FileMapper fileMapper
    ) {
        this.storageService = storageService;
        this.fileMapper = fileMapper;
    }

    @PostMapping("/upload")
    public List<UploadedFileResponse> upload(
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        return storageService
                .storeAll(files)
                .stream()
                .map(fileMapper::toUploadedFileResponse)
                .toList();
    }

    @DeleteMapping("/delete")
    public List<DeletedFileResponse> deleteAll(
            @RequestBody List<String> storageKeys
    ) {
        return storageService.deleteAll(storageKeys)
                .stream()
                .map(fileMapper::toDeletedFileResponse)
                .toList();
    }
}
