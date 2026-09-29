package com.rationportal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger logger =
            LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendPasswordResetEmail(
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
                "This token is valid for 15 minutes.\n\n" +
                "Use this token on the Ration Portal " +
                "password reset page.\n\n" +
                "If you did not request this password reset, " +
                "please ignore this email.\n\n" +
                "Regards,\n" +
                "Ration Portal\n" +
                "Public Distribution System"
        );

        try {

            mailSender.send(message);

            logger.info(
                    "Password reset email sent successfully to {}",
                    maskEmail(toEmail)
            );

            return true;

        } catch (MailException ex) {

            logger.error(
                    "Password reset email delivery failed for {}. " +
                    "The reset token remains stored securely in the database.",
                    maskEmail(toEmail),
                    ex
            );

            return false;
        }
    }

    private String maskEmail(String email) {

        if (email == null || email.isBlank()) {
            return "unknown";
        }

        int atIndex = email.indexOf('@');

        if (atIndex <= 1) {
            return "***";
        }

        return email.charAt(0)
                + "***"
                + email.substring(atIndex);
    }
}