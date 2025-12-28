package com.devteria.notification.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.devteria.notification.dto.EmailResponse;
import com.devteria.notification.dto.request.SendEmailRequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GmailService {
    private final JavaMailSender mailSender;

    String apiKey = "";

    public EmailResponse sendEmail(SendEmailRequest request) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getTo());
            message.setSubject(request.getSubject());
            message.setText(request.getHtmlContent());
            mailSender.send(message);
            // Trả về phản hồi thành công
            return EmailResponse.builder()
                    .messageId("Email sent successfully to " + request.getTo())
                    .build();
        } catch (MailException ex) {

            return EmailResponse.builder()
                    .messageId("Failed to send email: " + ex.getMessage())
                    .build();
        }
    }
}
