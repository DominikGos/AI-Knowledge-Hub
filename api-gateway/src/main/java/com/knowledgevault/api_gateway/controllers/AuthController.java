package com.knowledgevault.api_gateway.controllers;

import com.knowledgevault.api_gateway.dto.LoginRequest;
import com.knowledgevault.api_gateway.dto.LoginResponse;
import com.knowledgevault.api_gateway.dto.RegisterRequest;
import com.knowledgevault.api_gateway.dto.RegisterResponse;
import com.knowledgevault.api_gateway.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());

        return ResponseEntity
                .ok()
                .body(
                        new LoginResponse("Login successfully", token)
                );
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);

        return ResponseEntity
                .ok()
                .body(
                        new RegisterResponse("Register successfully, you can now login")
                );
    }
}
