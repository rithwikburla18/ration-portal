package com.rationportal.controller;

import com.rationportal.model.User;
import com.rationportal.repository.UserRepository;
import com.rationportal.security.JwtService;
import com.rationportal.service.MailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthControllerSecurityTests {
    private static final String PUBLIC_MESSAGE = "If an account exists for this email, password reset instructions will be provided.";

    @Test
    void forgotPasswordDoesNotRevealWhetherEmailExists() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        MailService mailService = mock(MailService.class);
        AuthController controller = new AuthController(repository, encoder, jwtService, authenticationManager, mailService);
        whenMissingEmail(repository);
        User known = new User();
        known.setEmail("known@example.com");
        known.setFullName("Test Citizen");
        org.mockito.Mockito.when(repository.findByEmail("known@example.com")).thenReturn(Optional.of(known));
        ResponseEntity<?> missing = controller.forgotPassword(Map.of("email", "missing@example.com"));
        ResponseEntity<?> exists = controller.forgotPassword(Map.of("email", "known@example.com"));
        assertEquals(200, missing.getStatusCode().value());
        assertEquals(200, exists.getStatusCode().value());
        assertEquals(Map.of("message", PUBLIC_MESSAGE), missing.getBody());
        assertEquals(missing.getBody(), exists.getBody());
        verify(mailService).sendPasswordResetEmail(eq("known@example.com"), anyString());
    }

    @Test
    void forgotPasswordKeepsGenericResponseWhenEmailDeliveryFails() {
        UserRepository repository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        MailService mailService = mock(MailService.class);
        AuthController controller = new AuthController(repository, encoder, jwtService, authenticationManager, mailService);
        User known = new User();
        known.setEmail("known@example.com");
        known.setFullName("Test Citizen");
        org.mockito.Mockito.when(repository.findByEmail("known@example.com")).thenReturn(Optional.of(known));
        doThrow(new IllegalStateException("private SMTP details")).when(mailService).sendPasswordResetEmail(eq("known@example.com"), anyString());
        ResponseEntity<?> response = controller.forgotPassword(Map.of("email", "known@example.com"));
        assertEquals(200, response.getStatusCode().value());
        assertEquals(Map.of("message", PUBLIC_MESSAGE), response.getBody());
    }

    @Test
    void sensitiveUserFieldsAreExcludedFromJsonSerialization() throws Exception {
        User user = new User();
        user.setFullName("Serialization Test");
        user.setEmail("serialization@example.com");
        user.setPasswordHash("HASH_SHOULD_NOT_LEAK");
        user.setResetPasswordToken("RESET_TOKEN_SHOULD_NOT_LEAK");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(mapper.writeValueAsString(user));

        assertTrue(json.has("email"));
        assertFalse(json.has("passwordHash"));
        assertFalse(json.has("resetPasswordToken"));
        assertFalse(json.has("resetPasswordTokenExpiry"));
        assertFalse(json.toString().contains("HASH_SHOULD_NOT_LEAK"));
        assertFalse(json.toString().contains("RESET_TOKEN_SHOULD_NOT_LEAK"));
    }
    private static void whenMissingEmail(UserRepository repository) {
        org.mockito.Mockito.when(repository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
    }
}