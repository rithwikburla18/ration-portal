package com.rationportal.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String toEmail,
            String resetToken) {

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
                "Your password reset token is:\n\n" +
                resetToken +
                "\n\n" +
                "Use this token on the Ration Portal " +
                "password reset page.\n\n" +
                "If you did not request this password reset, " +
                "please ignore this email.\n\n" +
                "Regards,\n" +
                "Ration Portal\n" +
                "Public Distribution System"
        );

        mailSender.send(message);
    }
}
