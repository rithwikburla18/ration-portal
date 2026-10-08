package com.rationportal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestPropertySource(properties = "app.jwt.secret=test-only-jwt-secret-123456789012345678901234567890")
@ActiveProfiles("test")
class RationPortalApplicationTests {

    @Test
    void applicationContextLoads() {
    }
}