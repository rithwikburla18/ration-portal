package com.rationportal.config;

import com.rationportal.model.User;
import com.rationportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    public AdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {

            System.out.println(
                    "ADMIN BOOTSTRAP SKIPPED: ADMIN_EMAIL or ADMIN_PASSWORD is not configured."
            );

            return;
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();

        if (adminPassword.length() < 8) {

            System.err.println(
                    "ADMIN BOOTSTRAP FAILED: ADMIN_PASSWORD must contain at least 8 characters."
            );

            return;
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElse(null);

        if (user == null) {

            user = new User();

            user.setFullName("Ration Portal Administrator");
            user.setEmail(normalizedEmail);
            user.setPasswordHash(
                    passwordEncoder.encode(adminPassword)
            );
            user.setRole("ADMIN");
            user.setStatus("ACTIVE");

            userRepository.save(user);

            System.out.println(
                    "ADMIN BOOTSTRAP SUCCESS: Admin account created."
            );

            return;
        }

        boolean changed = false;

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            user.setRole("ADMIN");
            changed = true;
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            user.setStatus("ACTIVE");
            changed = true;
        }

        if (changed) {

            userRepository.save(user);

            System.out.println(
                    "ADMIN BOOTSTRAP SUCCESS: Existing account promoted to ADMIN."
            );

        } else {

            System.out.println(
                    "ADMIN BOOTSTRAP: Admin account already exists."
            );
        }
    }
}