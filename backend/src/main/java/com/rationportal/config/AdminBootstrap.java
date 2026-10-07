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

    public AdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {
            System.out.println("ADMIN BOOTSTRAP SKIPPED: ADMIN_EMAIL or ADMIN_PASSWORD is not configured.");
            return;
        }

        String normalizedAdminEmail = adminEmail.trim().toLowerCase();

        if (adminPassword.length() < 8) {
            System.err.println("ADMIN BOOTSTRAP FAILED: ADMIN_PASSWORD must contain at least 8 characters.");
            return;
        }

        User admin = userRepository.findByEmail(normalizedAdminEmail).orElse(null);

        if (admin == null) {
            admin = new User();
            admin.setFullName("Ration Portal Administrator");
            admin.setEmail(normalizedAdminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        }

        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");
        userRepository.save(admin);

        for (User user : userRepository.findAll()) {
            if (!user.getEmail().equalsIgnoreCase(normalizedAdminEmail)
                    && "ADMIN".equalsIgnoreCase(user.getRole())) {
                user.setRole("CITIZEN");
                userRepository.save(user);
                System.out.println("ADMIN SECURITY: Demoted unauthorized ADMIN account: " + user.getEmail());
            }
        }

        System.out.println("ADMIN BOOTSTRAP SUCCESS: " + normalizedAdminEmail + " is the only authorized ADMIN.");
    }
}
