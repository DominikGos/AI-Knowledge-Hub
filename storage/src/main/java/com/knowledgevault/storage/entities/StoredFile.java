package com.knowledgevault.storage.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stored_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile {

    @Id
    private UUID id;

    @Column(name = "original_filename", nullable = false, length = 500)
    private String originalFilename;

    @Column(name = "storage_key", nullable = false, unique = true, length = 255)
    private String storageKey;

    @Column(name = "content_type", nullable = false, length = 255)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 30)
    private ProcessingStatus processingStatus;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}