package com.rationportal.controller;

import com.rationportal.model.User;
import com.rationportal.repository.UserRepository;
import com.rationportal.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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

        if (email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "available", false,
                            "message", "Please enter a valid email address."
                    )
            );
        }

        boolean exists =
                userRepository.findByEmail(email).isPresent();

        if (exists) {
            return ResponseEntity.ok(
                    Map.of(
                            "available", false,
                            "message", "Email already registered."
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "available", true,
                        "message", "Email is available."
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
}