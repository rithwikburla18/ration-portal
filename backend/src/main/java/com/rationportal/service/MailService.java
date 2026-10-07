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

    private static final String BREVO_API_URL =
            "https://api.brevo.com/v3/smtp/email";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${BREVO_API_KEY:}")
    private String brevoApiKey;

    @Value("${BREVO_FROM_EMAIL:}")
    private String brevoFromEmail;

    @Value("${FRONTEND_URL:https://ration-portal-frontend.onrender.com}")
    private String frontendUrl;

    public MailService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public void sendPasswordResetEmail(
            String email,
            String resetToken
    ) {

        if (brevoApiKey == null || brevoApiKey.isBlank()) {

            throw new IllegalStateException(
                    "BREVO_API_KEY is not configured."
            );
        }

        if (brevoFromEmail == null || brevoFromEmail.isBlank()) {

            throw new IllegalStateException(
                    "BREVO_FROM_EMAIL is not configured."
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

            Map<String, Object> sender = new HashMap<>();

            sender.put(
                    "name",
                    "Ration Portal"
            );

            sender.put(
                    "email",
                    brevoFromEmail
            );

            Map<String, Object> recipient = new HashMap<>();

            recipient.put(
                    "email",
                    email
            );

            Map<String, Object> payload = new HashMap<>();

            payload.put(
                    "sender",
                    sender
            );

            payload.put(
                    "to",
                    List.of(recipient)
            );

            payload.put(
                    "subject",
                    "Ration Portal - Password Reset"
            );

            payload.put(
                    "htmlContent",
                    html
            );

            String json =
                    objectMapper.writeValueAsString(payload);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            BREVO_API_URL
                                    )
                            )
                            .timeout(
                                    Duration.ofSeconds(30)
                            )
                            .header(
                                    "api-key",
                                    brevoApiKey
                            )
                            .header(
                                    "accept",
                                    "application/json"
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
                            HttpResponse.BodyHandlers
                                    .ofString()
                    );

            int statusCode =
                    response.statusCode();

            String responseBody =
                    response.body();

            System.out.println(
                    "BREVO HTTP STATUS: "
                            + statusCode
            );

            System.out.println(
                    "BREVO RESPONSE BODY: "
                            + responseBody
            );

            if (
                    statusCode < 200
                            || statusCode >= 300
            ) {

                throw new IllegalStateException(
                        "Brevo rejected email. HTTP "
                                + statusCode
                                + " Response: "
                                + responseBody
                );
            }

            System.out.println(
                    "PASSWORD RESET EMAIL SENT SUCCESSFULLY."
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Brevo email request was interrupted.",
                    exception
            );

        } catch (Exception exception) {

            System.err.println(
                    "BREVO EMAIL ERROR: "
                            + exception.getMessage()
            );

            throw new IllegalStateException(
                    "Password reset email could not be sent: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    public void sendLoginNotificationEmail(
            String email,
            String fullName
    ) {

        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            throw new IllegalStateException(
                    "BREVO_API_KEY is not configured."
            );
        }

        if (brevoFromEmail == null || brevoFromEmail.isBlank()) {
            throw new IllegalStateException(
                    "BREVO_FROM_EMAIL is not configured."
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email is required."
            );
        }

        String safeName =
                fullName == null || fullName.isBlank()
                        ? "Citizen"
                        : escapeHtml(fullName);

        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >
                    <title>Ration Portal Login Alert</title>
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

                    <div style="
                        padding:30px;
                    ">

                        <h2 style="
                            margin-top:0;
                            color:#111827;
                        ">
                            New Login Detected
                        </h2>

                        <p>
                            Dear %s,
                        </p>

                        <p>
                            Your Ration Portal account was successfully
                            signed in.
                        </p>

                        <div style="
                            margin:24px 0;
                            padding:18px;
                            background:#f7fafc;
                            border:1px solid #e5e7eb;
                            border-radius:8px;
                        ">
                            <strong>Account:</strong> %s<br>
                            <strong>Status:</strong> Successful login
                        </div>

                        <p>
                            If you made this login, no action is required.
                        </p>

                        <p>
                            If you did not sign in, please reset your
                            password immediately and secure your account.
                        </p>

                        <div style="
                            margin:28px 0;
                            text-align:center;
                        ">
                            <a
                                href="%s/pages/forgot-password.html"
                                style="
                                    display:inline-block;
                                    background:#0b5ed7;
                                    color:#ffffff;
                                    text-decoration:none;
                                    padding:14px 24px;
                                    border-radius:6px;
                                    font-weight:bold;
                                "
                            >
                                Secure My Account
                            </a>
                        </div>

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
                """.formatted(
                        safeName,
                        escapeHtml(email),
                        frontendUrl
                );

        try {

            Map<String, Object> sender = new HashMap<>();
            sender.put("name", "Ration Portal");
            sender.put("email", brevoFromEmail);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", email);

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", sender);
            payload.put("to", List.of(recipient));
            payload.put(
                    "subject",
                    "Ration Portal - New Login Alert"
            );
            payload.put("htmlContent", html);

            String json =
                    objectMapper.writeValueAsString(payload);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            BREVO_API_URL
                                    )
                            )
                            .timeout(
                                    Duration.ofSeconds(30)
                            )
                            .header(
                                    "api-key",
                                    brevoApiKey
                            )
                            .header(
                                    "accept",
                                    "application/json"
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
                            HttpResponse.BodyHandlers
                                    .ofString()
                    );

            int statusCode = response.statusCode();

            System.out.println(
                    "LOGIN ALERT EMAIL HTTP STATUS: "
                            + statusCode
            );

            if (statusCode < 200 || statusCode >= 300) {
                throw new IllegalStateException(
                        "Brevo rejected login alert email. HTTP "
                                + statusCode
                                + " Response: "
                                + response.body()
                );
            }

            System.out.println(
                    "LOGIN NOTIFICATION EMAIL SENT SUCCESSFULLY."
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Brevo login alert request was interrupted.",
                    exception
            );

        } catch (Exception exception) {

            System.err.println(
                    "LOGIN NOTIFICATION EMAIL ERROR: "
                            + exception.getMessage()
            );

            throw new IllegalStateException(
                    "Login notification email could not be sent: "
                            + exception.getMessage(),
                    exception
            );
        }
    }
    private String buildResetUrl(
            String resetToken
    ) {

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

    private String buildEmailHtml(
            String resetUrl
    ) {

        return """
                <!DOCTYPE html>

                <html lang="en">

                <head>

                    <meta charset="UTF-8">

                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >

                    <title>
                        Ration Portal Password Reset
                    </title>

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

                    <div style="
                        padding:30px;
                    ">

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

                            <a
                                href="%s"
                                style="
                                    display:inline-block;
                                    background:#0b5ed7;
                                    color:#ffffff;
                                    text-decoration:none;
                                    padding:14px 24px;
                                    border-radius:6px;
                                    font-weight:bold;
                                "
                            >
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
                """.formatted(
                escapeHtml(resetUrl)
        );
    }

    private String escapeHtml(
            String value
    ) {

        return value
                .replace(
                        "&",
                        "&amp;"
                )
                .replace(
                        "\"",
                        "&quot;"
                )
                .replace(
                        "<",
                        "&lt;"
                )
                .replace(
                        ">",
                        "&gt;"
                );
    }
}