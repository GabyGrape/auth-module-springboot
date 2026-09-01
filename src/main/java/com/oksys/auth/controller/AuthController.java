package com.oksys.auth.controller;

import com.oksys.auth.dto.*;
import com.oksys.auth.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Registrasi berhasil! Silakan cek email Anda untuk kode verifikasi OTP.")
                        .data(request.getEmail())
                        .build()
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyAccount(request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Akun berhasil diverifikasi! Silakan login.")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login berhasil!")
                        .data(response)
                        .build()
        );
    }
}