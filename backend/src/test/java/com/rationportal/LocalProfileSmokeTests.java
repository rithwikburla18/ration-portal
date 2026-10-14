package com.rationportal;

import com.rationportal.model.User;
import com.rationportal.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "app.jwt.secret=local-profile-test-only-secret-12345678901234567890",
        "ADMIN_EMAIL=local-admin@example.test",
        "ADMIN_PASSWORD=local-profile-test-password-123"
})
@ActiveProfiles("local")
class LocalProfileSmokeTests {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private UserRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void localProfileUsesIsolatedH2AndCreatesTemporaryAdmin() {
        assertEquals(1, jdbc.queryForObject("SELECT 1", Integer.class));
        assertEquals(0L, jdbc.queryForObject("SELECT COUNT(*) FROM audit_logs", Long.class));
        assertEquals(0L, jdbc.queryForObject("SELECT COUNT(*) FROM notification_log", Long.class));

        User admin = users.findByEmail("local-admin@example.test").orElseThrow();
        assertEquals("ADMIN", admin.getRole());
        assertEquals("ACTIVE", admin.getStatus());
        assertTrue(passwordEncoder.matches("local-profile-test-password-123", admin.getPasswordHash()));
    }
}
