package com.rationportal.controller;

import com.rationportal.model.User;
import com.rationportal.repository.UserRepository;
import com.rationportal.service.MailService;
import com.rationportal.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            MailService mailService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(
            @RequestBody Map<String, String> request
    ) {

        String email = request
                .getOrDefault("email", "")
                .trim()
                .toLowerCase();

        if (email.isBlank()
                || !EMAIL_PATTERN.matcher(email).matches()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "available", false,
                            "message",
                            "Please enter a valid email address."
                    )
            );
        }

        boolean exists =
                userRepository.findByEmail(email).isPresent();

        if (exists) {

            return ResponseEntity.ok(
                    Map.of(
                            "available", false,
                            "message",
                            "Email already registered."
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "available", true,
                        "message",
                        "Email is available."
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> request
    ) {

        String name =
                request.getOrDefault("fullName", "")
                        .trim();

        String email =
                request.getOrDefault("email", "")
                        .trim()
                        .toLowerCase();

        String password =
                request.getOrDefault("password", "");

        String termsAccepted =
                request.getOrDefault(
                        "termsAccepted",
                        "false"
                );

        /*
         * ========================================================
         * TERMS & CONDITIONS - SERVER-SIDE ENFORCEMENT
         * ========================================================
         *
         * A citizen account cannot be created unless the
         * Terms & Conditions have explicitly been accepted.
         */
        if (!"true".equalsIgnoreCase(
                termsAccepted.trim()
        )) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Terms & Conditions acceptance is required."
                    )
            );
        }

        if (name.isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Full name is required."
                    )
            );
        }

        if (email.isBlank()
                || !EMAIL_PATTERN.matcher(email).matches()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Please enter a valid email address."
                    )
            );
        }

        if (password.length() < 8) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Password must contain at least 8 characters."
                    )
            );
        }

        if (userRepository.findByEmail(email).isPresent()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Email already registered."
                    )
            );
        }

        User user = new User();

        user.setFullName(name);
        user.setEmail(email);

        user.setPasswordHash(
                passwordEncoder.encode(password)
        );

        /*
         * Citizen self-registration can NEVER create
         * an administrator account.
         */
        user.setRole("CITIZEN");
        user.setStatus("ACTIVE");

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Registration successful."
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request
    ) {

        String email =
                request.getOrDefault("email", "")
                        .trim()
                        .toLowerCase();

        String password =
                request.getOrDefault("password", "");

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );

            User user =
                    userRepository
                            .findByEmail(email)
                            .orElseThrow();

            String token =
                    jwtService.generateToken(
                            user.getEmail(),
                            user.getRole()
                    );

            /*
             * Security notification after successful
             * authentication.
             *
             * Email failure must never block login.
             */
            try {

                mailService.sendLoginNotificationEmail(
                        user.getEmail(),
                        user.getFullName()
                );

            } catch (Exception mailException) {

                System.err.println(
                        "LOGIN NOTIFICATION WARNING: "
                                + mailException.getMessage()
                );
            }

            return ResponseEntity.ok(
                    Map.of(
                            "token", token,
                            "fullName", user.getFullName(),
                            "role", user.getRole()
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(401)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email or password"
                            )
                    );
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            org.springframework.security.core.Authentication authentication
    ) {

        if (
                authentication == null ||
                !authentication.isAuthenticated()
        ) {

            return ResponseEntity
                    .status(401)
                    .body(
                            Map.of(
                                    "message",
                                    "Authentication required."
                            )
                    );
        }

        User user =
                userRepository
                        .findByEmail(
                                authentication
                                        .getName()
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .status(401)
                    .body(
                            Map.of(
                                    "message",
                                    "Authenticated user not found."
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "email",
                        user.getEmail(),

                        "fullName",
                        user.getFullName(),

                        "role",
                        user.getRole(),

                        "status",
                        user.getStatus()
                )
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> request
    ) {

        String email =
                request.getOrDefault("email", "")
                        .trim()
                        .toLowerCase();

        if (
                email.isBlank()
                        || !EMAIL_PATTERN
                        .matcher(email)
                        .matches()
        ) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Please enter a valid email address."
                    )
            );
        }

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        final String publicMessage = "If an account exists for this email, password reset instructions will be provided.";

        /* Use the same public response for registered and unknown email addresses. */
        if (user == null) {
            return ResponseEntity.ok(Map.of("message", publicMessage));
        }

        String resetToken =
                UUID.randomUUID().toString();

        LocalDateTime expiry =
                LocalDateTime.now()
                        .plusMinutes(15);

        user.setResetPasswordToken(
                resetToken
        );

        user.setResetPasswordTokenExpiry(
                expiry
        );

        userRepository.save(user);

        try {
            mailService.sendPasswordResetEmail(email, resetToken);
        } catch (Exception mailException) {
            System.err.println("PASSWORD RESET EMAIL WARNING: delivery failed (" + mailException.getClass().getSimpleName() + ").");
        }

        return ResponseEntity.ok(Map.of("message", publicMessage));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> request
    ) {

        String token =
                request.getOrDefault(
                        "token",
                        ""
                )
                        .trim();

        String newPassword =
                request.getOrDefault(
                        "newPassword",
                        ""
                );

        String termsAccepted =
                request.getOrDefault(
                        "termsAccepted",
                        "false"
                );

        /*
         * ========================================================
         * TERMS & CONDITIONS - SERVER-SIDE ENFORCEMENT
         * ========================================================
         *
         * Changing a password through the password-reset
         * workflow requires explicit Terms acceptance.
         */
        if (!"true".equalsIgnoreCase(
                termsAccepted.trim()
        )) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Terms & Conditions acceptance is required."
                    )
            );
        }

        if (token.isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Reset token is required."
                    )
            );
        }

        if (
                newPassword.isBlank()
                        || newPassword.length() < 8
        ) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Password must be at least 8 characters long."
                    )
            );
        }

        User user =
                userRepository
                        .findByResetPasswordToken(token)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Invalid or expired reset token."
                    )
            );
        }

        if (
                user.getResetPasswordTokenExpiry() == null
                        || user
                        .getResetPasswordTokenExpiry()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Invalid or expired reset token."
                    )
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(
                        newPassword
                )
        );

        /*
         * Reset tokens are single-use.
         */
        user.setResetPasswordToken(
                null
        );

        user.setResetPasswordTokenExpiry(
                null
        );

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset successfully."
                )
        );
    }
}