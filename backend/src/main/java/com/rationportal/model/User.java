package com.rationportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@Column(name = "full_name", nullable = false)
private String fullName;
@Column(nullable = false, unique = true)
private String email;
@Column(name = "password_hash", nullable = false)
private String passwordHash;
@Column(nullable = false)
private String role;
@Column(nullable = false)
private String status;
@Column(name = "created_at", nullable = false)
private LocalDateTime createdAt;
@PrePersist
protected void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); if (role == null || role.isBlank()) role = "CITIZEN"; if (status == null || status.isBlank()) status = "ACTIVE"; }
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }
public String getFullName() { return fullName; }
public void setFullName(String fullName) { this.fullName = fullName; }
public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public String getPasswordHash() { return passwordHash; }
public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
public String getRole() { return role; }
public void setRole(String role) { this.role = role; }
public String getStatus() { return status; }
public void setStatus(String status) { this.status = status; }
public LocalDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}