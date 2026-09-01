package com.oksys.auth.service.impl;

import com.oksys.auth.dto.AuthResponse;
import com.oksys.auth.dto.LoginRequest;
import com.oksys.auth.dto.RegisterRequest;
import com.oksys.auth.dto.VerifyOtpRequest;
import com.oksys.auth.model.Role; // Sesuaikan import Role dengan lokasi paketmu
import com.oksys.auth.model.User; // Sesuaikan import User dengan lokasi paketmu
import com.oksys.auth.model.VerificationCode;
import com.oksys.auth.repository.UserRepository;
import com.oksys.auth.repository.VerificationCodeRepository;
import com.oksys.auth.utils.JwtUtils; // Sesuaikan import JwtUtils dengan lokasi paketmu
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .enabled(false) // User belum aktif sampai verifikasi OTP selesai
                .build();

        userRepository.save(user);

        // Generate 6-digit OTP
        String otpCode = generateOtpCode();

        VerificationCode verificationCode = VerificationCode.builder()
                .user(user)
                .code(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        verificationCodeRepository.save(verificationCode);

        // Kirim Email secara Async via SMTP
        emailService.sendVerificationEmail(user.getEmail(), otpCode);
    }

    @Transactional
    public void verifyAccount(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email tidak ditemukan"));

        if (user.isEnabled()) {
            throw new IllegalStateException("Akun sudah aktif");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByUserAndCode(user, request.getCode())
                .orElseThrow(() -> new IllegalArgumentException("Kode OTP tidak valid"));

        if (verificationCode.isExpired()) {
            throw new IllegalStateException("Kode OTP telah kedaluwarsa");
        }

        // Aktifkan akun user
        user.setEnabled(true);
        userRepository.save(user);

        // Hapus kode verifikasi yang telah terpakai
        verificationCodeRepository.deleteByUser(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // 1. Verifikasi Username & Password via AuthenticationManager
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (DisabledException e) {
            throw new IllegalStateException("Akun belum diverifikasi. Silakan cek email Anda untuk kode OTP.");
        } catch (AuthenticationException e) {
            throw new IllegalArgumentException("Username atau password salah!");
        }

        // 2. Cari user di database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan: " + request.getUsername()));

        // 3. Generate token JWT
        String token = jwtUtils.generateToken(user);
        return new AuthResponse(token, user.getUsername());
    }

    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}