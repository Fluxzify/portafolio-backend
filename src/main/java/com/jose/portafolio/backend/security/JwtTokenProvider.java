package com.jose.portafolio.backend.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts.SIG;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final long jwtExpirationMs = 86400000; // 1 día

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey()) // HS256 desde la nueva API
                .compact();
    }

    public String getEmailFromToken(String token) {
        JwtParser parser = Jwts.parser().verifyWith((SecretKey) getSigningKey()).build();
        return parser.parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parser().verifyWith((SecretKey) getSigningKey()).build();
            parser.parseSignedClaims(token); // esto lanza excepción si no es válido
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Token inválido: " + token);
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
