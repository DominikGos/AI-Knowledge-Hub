package com.knowledgevault.storage.controllers;

import com.knowledgevault.storage.dto.UploadedFileResponse;
import com.knowledgevault.storage.services.StorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/storage")
public class StorageController {
    private StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public List<UploadedFileResponse> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {
        return storageService.storeAll(files);
    }
}
