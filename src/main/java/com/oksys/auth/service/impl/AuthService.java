package com.oksys.auth.service.impl;

import com.oksys.auth.dto.AuthResponse;
import com.oksys.auth.dto.LoginRequest;
import com.oksys.auth.dto.RegisterRequest;
import com.oksys.auth.model.Role;
import com.oksys.auth.model.User;
import com.oksys.auth.repository.UserRepository;
import com.oksys.auth.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username sudah digunakan!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email sudah digunakan!");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Enkripsi password menggunakan BCrypt
                .role(Role.ROLE_USER) // Default role
                .build();

        userRepository.save(user);

        return "User berhasil terdaftar!";
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Verifikasi Username & Password lewat AuthenticationManager
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            System.err.println("❌ GAGAL AUTENTIKASI (Username/Password Salah): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Username atau password salah!");
        }

        // 2. Cari user di database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan dengan username: " + request.getUsername()));

        // 3. Generate token JWT
        try {
            String token = jwtUtils.generateToken(user);
            return new AuthResponse(token, user.getUsername());
        } catch (Exception e) {
            System.err.println("🔥 MASALAH JWT: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal me-generate JWT token: " + e.getMessage());
        }
    }
}