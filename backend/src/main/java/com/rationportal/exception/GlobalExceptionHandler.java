package com.rationportal.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> malformedRequest(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Malformed request body.", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> invalidParameter(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Invalid request parameter.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return response(HttpStatus.FORBIDDEN, "Access denied.", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> invalidArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage() == null ? "Invalid request." : ex.getMessage(), request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtime(RuntimeException ex, HttpServletRequest request) {
        String message = ex.getMessage() == null ? "Request could not be processed." : ex.getMessage();
        String lower = message.toLowerCase();
        HttpStatus status = lower.contains("not authorized") || lower.contains("not owned") || lower.contains("not approved")
                ? HttpStatus.FORBIDDEN
                : lower.contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        return response(status, status == HttpStatus.INTERNAL_SERVER_ERROR ? "Request could not be processed." : message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unexpected(Exception ex, HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred.", request);
    }

    private ResponseEntity<?> response(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}