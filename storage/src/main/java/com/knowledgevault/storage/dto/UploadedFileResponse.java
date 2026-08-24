package com.knowledgevault.storage.dto;

import com.knowledgevault.storage.entities.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileResponse {

    private UUID id;

    private String originalFilename;

    private String storageKey;

    private String contentType;

    private long size;

    private ProcessingStatus processingStatus;

    private Instant createdAt;

    private Instant updatedAt;
}