package com.rationportal.controller;

import com.rationportal.model.User;
import com.rationportal.repository.UserRepository;
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

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> request
    ) {

        String email =
                request.getOrDefault("email", "")
                        .trim()
                        .toLowerCase();

        if (email.isBlank()
                || !EMAIL_PATTERN.matcher(email).matches()) {

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

        /*
         * Do not reveal whether an email is registered.
         * This prevents account/email enumeration.
         */
        if (user == null) {

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "If an account exists for this email, password reset instructions will be provided."
                    )
            );
        }

        String resetToken =
                UUID.randomUUID().toString();

        LocalDateTime expiry =
                LocalDateTime.now().plusMinutes(15);

        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiry(expiry);

        userRepository.save(user);

        /*
         * Email delivery will be connected in the next step.
         * For now, the token is returned only so the reset flow
         * can be tested end-to-end.
         */
        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset request created.",
                        "resetToken",
                        resetToken
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> request
    ) {

        String token =
                request.getOrDefault("token", "")
                        .trim();

        String newPassword =
                request.getOrDefault("newPassword", "");

        if (token.isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Reset token is required."
                    )
            );
        }

        if (newPassword.isBlank()
                || newPassword.length() < 8) {

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

        if (user.getResetPasswordTokenExpiry() == null
                || user.getResetPasswordTokenExpiry()
                        .isBefore(LocalDateTime.now())) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            "Invalid or expired reset token."
                    )
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(newPassword)
        );

        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset successfully."
                )
        );
    }
}