package com.knowledgevault.api_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class AuthErrorResponse {

    private String message;
    private Instant timestamp;

    public AuthErrorResponse(String message) {
        this.message = message;
        this.timestamp = Instant.now();
    }
}
