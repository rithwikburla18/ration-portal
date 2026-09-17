package com.rationportal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET = "RationPortalEducationalProjectSecretKey2026ChangeThisInProduction";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    public String generateToken(String email, String role) { return Jwts.builder().subject(email).claim("role", role).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 86400000)).signWith(key).compact(); }
    public String extractUsername(String token) { return parseClaims(token).getSubject(); }
    public boolean isTokenValid(String token, String email) { try { Claims claims=parseClaims(token); return claims.getSubject().equals(email) && claims.getExpiration().after(new Date()); } catch(Exception e) { return false; } }
    private Claims parseClaims(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
