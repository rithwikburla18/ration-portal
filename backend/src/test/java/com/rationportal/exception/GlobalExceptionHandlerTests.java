
package com.rationportal.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTests {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void databaseConflictReturns409WithoutExposingDatabaseDetails() {
        HttpServletRequest request =
                new MockHttpServletRequest("POST", "/api/ration-cards");

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "unique constraint users_email_key; confidential database details");

        ResponseEntity<?> response = handler.dataConflict(exception, request);

        assertEquals(409, response.getStatusCode().value());

        Object responseBody = Objects.requireNonNull(
                response.getBody(),
                "Expected exception response body");

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) responseBody;

        assertEquals(
                "The request conflicts with existing data.",
                body.get("message"));

        assertFalse(body.toString().contains("confidential database details"));

        assertEquals("/api/ration-cards", body.get("path"));
    }
}
