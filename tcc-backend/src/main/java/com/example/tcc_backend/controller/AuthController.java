package com.example.tcc_backend.controller;

import com.example.tcc_backend.dto.request.ChangePasswordRequest;
import com.example.tcc_backend.dto.request.LoginRequest;
import com.example.tcc_backend.dto.request.RegisterRequest;
import com.example.tcc_backend.dto.response.AuthResponse;
import com.example.tcc_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest dto) {
        return ResponseEntity.ok(service.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest dto) {
        return ResponseEntity.ok(service.login(dto));
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest dto) {
        service.changePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok(service.logout());
    }
}
