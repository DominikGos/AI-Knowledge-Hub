package com.knowledgevault.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StorageServiceErrorResponse {

    private final String message;

    private final Instant timestamp;
}
