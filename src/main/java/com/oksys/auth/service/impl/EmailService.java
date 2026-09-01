package com.oksys.auth.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Kode Verifikasi Registrasi Ticket Service");

            // Standardisasi tampilan email menggunakan HTML
            String htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                    <h2>Selamat Datang di Ticket Service!</h2>
                    <p>Terima kasih telah mendaftar. Gunakan kode verifikasi di bawah ini untuk mengaktifkan akun Anda:</p>
                    <div style="background-color: #f4f4f4; padding: 10px 20px; font-size: 24px; font-weight: bold; letter-spacing: 4px; display: inline-block; margin: 10px 0;">
                        %s
                    </div>
                    <p>Kode ini berlaku selama <strong>10 menit</strong>.</p>
                    <hr/>
                    <p style="font-size: 12px; color: #777;">Jika Anda tidak merasa mendaftar, abaikan email ini.</p>
                </div>
                """.formatted(code);

            helper.setText(htmlContent, true); // parameter true = kirim sebagai HTML

            mailSender.send(message);
            log.info("Email verifikasi berhasil dikirim ke: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Gagal mengirim email verifikasi ke {}: {}", toEmail, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Terjadi kesalahan tidak terduga saat mengirim email: {}", e.getMessage(), e);
        }
    }
}