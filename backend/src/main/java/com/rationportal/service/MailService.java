package com.rationportal.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    private static final String FRONTEND_RESET_URL =
            "https://ration-portal-frontend.onrender.com/pages/reset-password.html";

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String toEmail,
            String resetToken) {

        String resetLink =
                FRONTEND_RESET_URL + "?token=" + resetToken;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject(
                "Ration Portal - Password Reset"
        );

        message.setText(
                "Dear Citizen,\n\n" +

                "A password reset request was created " +
                "for your Ration Portal account.\n\n" +

                "Use the secure link below to reset your password:\n\n" +

                resetLink + "\n\n" +

                "This password reset link is valid for 15 minutes " +
                "and can only be used once.\n\n" +

                "If you did not request this password reset, " +
                "please ignore this email.\n\n" +

                "Regards,\n" +
                "Ration Portal\n" +
                "Public Distribution System"
        );

        mailSender.send(message);
    }
}