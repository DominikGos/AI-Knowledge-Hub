package com.knowledgevault.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileResponse {

    private String originalFilename;

    private String storageKey;

    private String contentType;

    private long size;
}