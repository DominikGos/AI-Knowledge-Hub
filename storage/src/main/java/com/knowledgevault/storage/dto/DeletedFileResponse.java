package com.knowledgevault.storage.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletedFileResponse {
    private UUID id;

    private String originalFilename;

    private String storageKey;
}
