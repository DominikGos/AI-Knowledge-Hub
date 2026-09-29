package com.knowledgevault.api_gateway.controllers;

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
    public String login(@RequestParam String email, @RequestParam String password) {
        return authService.login(email, password);
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
