package com.knowledgevault.api_gateway.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String message;
    private String token;
    private Instant timestamp;

    public LoginResponse(String message, String token) {
        this.message = message;
        this.token = token;

        this.timestamp = Instant.now();
    }
}