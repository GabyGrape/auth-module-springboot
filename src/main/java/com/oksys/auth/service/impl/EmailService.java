package com.oksys.auth.service.impl;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    private Resend resend;

    @PostConstruct
    public void init() {
        this.resend = new Resend(apiKey);
    }

    @Async
    public void sendVerificationEmail(String toEmail, String code) {
        try {
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

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("noreply@smtpmail.oktagabriel.my.id")
                    .to(toEmail)
                    .subject("Kode Verifikasi Registrasi Ticket Service")
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            log.info("Email verifikasi berhasil dikirim ke: {} (ID: {})", toEmail, response.getId());

        } catch (ResendException e) {
            log.error("Gagal mengirim email verifikasi ke {} via Resend API: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            log.error("Terjadi kesalahan tidak terduga saat mengirim email: {}", e.getMessage());
        }
    }
}