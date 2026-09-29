package com.rationportal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailService {

    private static final String RESEND_API_URL =
            "https://api.resend.com/emails";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${resend.api-key:}")
    private String resendApiKey;

    @Value("${resend.from:onboarding@resend.dev}")
    private String resendFrom;

    @Value("${app.frontend-url:https://ration-portal-frontend.onrender.com}")
    private String frontendUrl;

    public MailService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void sendPasswordResetEmail(
            String email,
            String resetToken) {

        if (resendApiKey == null || resendApiKey.isBlank()) {
            throw new IllegalStateException(
                    "Password reset email service is not configured."
            );
        }

        if (resendFrom == null || resendFrom.isBlank()) {
            throw new IllegalStateException(
                    "Password reset sender is not configured."
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email is required."
            );
        }

        if (resetToken == null || resetToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Password reset token is required."
            );
        }

        String resetUrl = buildResetUrl(resetToken);

        String html = buildEmailHtml(resetUrl);

        try {
            Map<String, Object> payload = new HashMap<>();

            payload.put("from", resendFrom);
            payload.put("to", List.of(email));
            payload.put(
                    "subject",
                    "Ration Portal - Password Reset"
            );
            payload.put("html", html);

            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_API_URL))
                    .timeout(Duration.ofSeconds(20))
                    .header(
                            "Authorization",
                            "Bearer " + resendApiKey
                    )
                    .header(
                            "Content-Type",
                            "application/json"
                    )
                    .POST(
                            HttpRequest.BodyPublishers
                                    .ofString(json)
                    )
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            int statusCode = response.statusCode();

            if (statusCode < 200 || statusCode >= 300) {
                throw new IllegalStateException(
                        "Password reset email could not be sent."
                );
            }

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Password reset email request was interrupted."
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Password reset email could not be sent."
            );
        }
    }

    private String buildResetUrl(String resetToken) {

        String baseUrl = frontendUrl;

        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(
                    0,
                    baseUrl.length() - 1
            );
        }

        return baseUrl
                + "/pages/reset-password.html?token="
                + resetToken;
    }

    private String buildEmailHtml(String resetUrl) {

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">
                    <title>Ration Portal Password Reset</title>
                </head>

                <body style="
                    margin:0;
                    padding:0;
                    background:#f4f6f8;
                    font-family:Arial,Helvetica,sans-serif;
                    color:#1f2937;
                ">

                <div style="
                    max-width:600px;
                    margin:40px auto;
                    background:#ffffff;
                    border:1px solid #d9dee5;
                    border-radius:10px;
                    overflow:hidden;
                ">

                    <div style="
                        background:#0b5ed7;
                        color:#ffffff;
                        padding:24px;
                        text-align:center;
                    ">
                        <h1 style="
                            margin:0;
                            font-size:24px;
                        ">
                            Ration Portal
                        </h1>

                        <p style="
                            margin:8px 0 0;
                            font-size:14px;
                        ">
                            Public Distribution System
                        </p>
                    </div>

                    <div style="padding:30px;">

                        <h2 style="
                            margin-top:0;
                            color:#111827;
                        ">
                            Password Reset Request
                        </h2>

                        <p>
                            Dear Citizen,
                        </p>

                        <p>
                            A password reset request was created
                            for your Ration Portal account.
                        </p>

                        <p>
                            Click the button below to securely
                            reset your password.
                        </p>

                        <div style="
                            text-align:center;
                            margin:30px 0;
                        ">

                            <a href="%s"
                               style="
                                   display:inline-block;
                                   background:#0b5ed7;
                                   color:#ffffff;
                                   text-decoration:none;
                                   padding:14px 24px;
                                   border-radius:6px;
                                   font-weight:bold;
                               ">
                                Reset Password
                            </a>

                        </div>

                        <p style="
                            font-size:13px;
                            color:#6b7280;
                        ">
                            This password reset link is temporary
                            and should only be used by you.
                        </p>

                        <p style="
                            font-size:13px;
                            color:#6b7280;
                        ">
                            If you did not request this password
                            reset, please ignore this email.
                        </p>

                        <hr style="
                            border:none;
                            border-top:1px solid #e5e7eb;
                            margin:28px 0;
                        ">

                        <p style="
                            margin-bottom:0;
                            font-size:13px;
                            color:#6b7280;
                        ">
                            Regards,<br>
                            <strong>Ration Portal</strong><br>
                            Public Distribution System
                        </p>

                    </div>
                </div>

                </body>
                </html>
                """.formatted(escapeHtml(resetUrl));
    }

    private String escapeHtml(String value) {

        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}