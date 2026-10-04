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
public class NotificationService {

    private static final String BREVO_EMAIL_API =
            "https://api.brevo.com/v3/smtp/email";

    private static final String BREVO_SMS_API =
            "https://api.brevo.com/v3/transactionalSMS/send";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${BREVO_API_KEY:}")
    private String brevoApiKey;

    @Value("${BREVO_FROM_EMAIL:}")
    private String brevoFromEmail;

    @Value("${BREVO_SMS_SENDER:RationPortal}")
    private String brevoSmsSender;

    public NotificationService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public void sendEmail(
            String recipientEmail,
            String subject,
            String htmlContent
    ) {

        validateEmailConfiguration();

        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email is required."
            );
        }

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException(
                    "Email subject is required."
            );
        }

        if (htmlContent == null || htmlContent.isBlank()) {
            throw new IllegalArgumentException(
                    "Email content is required."
            );
        }

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
                    recipientEmail
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
                    subject
            );

            payload.put(
                    "htmlContent",
                    htmlContent
            );

            String json =
                    objectMapper.writeValueAsString(payload);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            BREVO_EMAIL_API
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

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new IllegalStateException(
                        "Brevo email rejected request. HTTP "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            System.out.println(
                    "NOTIFICATION EMAIL SENT. HTTP STATUS: "
                            + response.statusCode()
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Email notification was interrupted.",
                    exception
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Email notification failed.",
                    exception
            );
        }
    }

    public void sendSms(
            String recipientMobile,
            String message
    ) {

        validateSmsConfiguration();

        if (recipientMobile == null
                || recipientMobile.isBlank()) {

            throw new IllegalArgumentException(
                    "Recipient mobile number is required."
            );
        }

        if (message == null || message.isBlank()) {

            throw new IllegalArgumentException(
                    "SMS message is required."
            );
        }

        try {

            Map<String, Object> payload =
                    new HashMap<>();

            payload.put(
                    "sender",
                    brevoSmsSender
            );

            payload.put(
                    "recipient",
                    normalizeIndianMobileNumber(
                            recipientMobile
                    )
            );

            payload.put(
                    "content",
                    message
            );

            payload.put(
                    "type",
                    "transactional"
            );

            payload.put(
                    "unicodeEnabled",
                    true
            );

            payload.put(
                    "tag",
                    "ration-portal"
            );

            String json =
                    objectMapper.writeValueAsString(payload);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            BREVO_SMS_API
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

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new IllegalStateException(
                        "Brevo SMS rejected request. HTTP "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            System.out.println(
                    "NOTIFICATION SMS SENT. HTTP STATUS: "
                            + response.statusCode()
            );

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "SMS notification was interrupted.",
                    exception
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "SMS notification failed.",
                    exception
            );
        }
    }

    public void sendApplicationSubmittedEmail(
            String email,
            String applicantName,
            String applicationNumber
    ) {

        String safeName =
                escapeHtml(applicantName);

        String safeApplicationNumber =
                escapeHtml(applicationNumber);

        String html =
                """
                <html>
                <body style="font-family:Arial,sans-serif;background:#f5f7fa;padding:30px;">
                    <div style="max-width:650px;margin:auto;background:#ffffff;padding:30px;border-radius:12px;">
                        <h2 style="color:#0b4f8a;">
                            Ration Portal
                        </h2>

                        <p>Dear %s,</p>

                        <p>
                            Your ration card application has been
                            successfully submitted.
                        </p>

                        <p>
                            <strong>Application Number:</strong>
                            %s
                        </p>

                        <p>
                            Please keep this application number safe
                            for future status tracking.
                        </p>

                        <hr>

                        <p style="color:#6b7280;font-size:13px;">
                            This is an automated message from Ration Portal.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(
                        safeName,
                        safeApplicationNumber
                );

        sendEmail(
                email,
                "Ration Portal - Application Submitted",
                html
        );
    }

    public void sendApplicationSubmittedSms(
            String mobile,
            String applicationNumber
    ) {

        String message =
                "Ration Portal: Your ration card application "
                        + applicationNumber
                        + " has been submitted successfully. "
                        + "Keep this number for status tracking.";

        sendSms(
                mobile,
                message
        );
    }

    public void sendApplicationStatusEmail(
            String email,
            String applicantName,
            String applicationNumber,
            String status
    ) {

        String safeName =
                escapeHtml(applicantName);

        String safeApplicationNumber =
                escapeHtml(applicationNumber);

        String safeStatus =
                escapeHtml(status);

        String html =
                """
                <html>
                <body style="font-family:Arial,sans-serif;background:#f5f7fa;padding:30px;">
                    <div style="max-width:650px;margin:auto;background:#ffffff;padding:30px;border-radius:12px;">
                        <h2 style="color:#0b4f8a;">
                            Ration Portal
                        </h2>

                        <p>Dear %s,</p>

                        <p>
                            Your ration card application status has been updated.
                        </p>

                        <p>
                            <strong>Application Number:</strong>
                            %s
                        </p>

                        <p>
                            <strong>Current Status:</strong>
                            %s
                        </p>

                        <p>
                            Please log in to the Ration Portal to view
                            the latest application details.
                        </p>

                        <hr>

                        <p style="color:#6b7280;font-size:13px;">
                            This is an automated message from Ration Portal.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(
                        safeName,
                        safeApplicationNumber,
                        safeStatus
                );

        sendEmail(
                email,
                "Ration Portal - Application Status Updated",
                html
        );
    }

    public void sendApplicationStatusSms(
            String mobile,
            String applicationNumber,
            String status
    ) {

        String message =
                "Ration Portal: Application "
                        + applicationNumber
                        + " status updated to "
                        + status
                        + ". Please check the portal for details.";

        sendSms(
                mobile,
                message
        );
    }

    private void validateEmailConfiguration() {

        if (brevoApiKey == null
                || brevoApiKey.isBlank()) {

            throw new IllegalStateException(
                    "BREVO_API_KEY is not configured."
            );
        }

        if (brevoFromEmail == null
                || brevoFromEmail.isBlank()) {

            throw new IllegalStateException(
                    "BREVO_FROM_EMAIL is not configured."
            );
        }
    }

    private void validateSmsConfiguration() {

        if (brevoApiKey == null
                || brevoApiKey.isBlank()) {

            throw new IllegalStateException(
                    "BREVO_API_KEY is not configured."
            );
        }

        if (brevoSmsSender == null
                || brevoSmsSender.isBlank()) {

            throw new IllegalStateException(
                    "BREVO_SMS_SENDER is not configured."
            );
        }
    }

    private String normalizeIndianMobileNumber(
            String mobile
    ) {

        String value =
                mobile.trim()
                        .replaceAll("[^0-9+]", "");

        if (value.startsWith("+")) {

            return value;
        }

        if (value.startsWith("91")
                && value.length() == 12) {

            return "+" + value;
        }

        if (value.length() == 10) {

            return "+91" + value;
        }

        return value;
    }

    private String escapeHtml(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}