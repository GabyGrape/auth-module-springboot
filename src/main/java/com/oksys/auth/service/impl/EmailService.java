package com.oksys.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Kode Verifikasi Registrasi Ticket Service");
        message.setText("Terima kasih telah mendaftar. Kode verifikasi OTP Anda adalah: " + code
                + "\n\nKode ini berlaku selama 10 menit.");

        mailSender.send(message);
    }
}