package com.knowledgevault.api_gateway.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {

    private String message;
    private Instant timestamp;
    public RegisterResponse(String message) {
        this.message = message;
        this.timestamp = Instant.now();
    }
}
