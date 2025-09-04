package com.jose.portafolio.backend.controller;

import com.jose.portafolio.backend.dto.JwtResponse;
import com.jose.portafolio.backend.dto.LoginRequest;
import com.jose.portafolio.backend.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.web.util.WebUtils;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(false) // solo test
                    .path("/")
                    .maxAge(24*60*60)
                    .sameSite("Lax") // temporal
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
            System.out.println(cookie);
            return ResponseEntity.ok(Map.of("message", "Login exitoso"));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            // Aquí capturamos la excepción de credenciales inválidas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales incorrectas"));
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getUsuarioAutenticado(HttpServletRequest request) {
        var cookie = WebUtils.getCookie(request, "token");
        if (cookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado: token no presente"));
        }

        String token = cookie.getValue();
        System.out.println("Token recibido: " + token);

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado: token inválido o expirado"));
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        return ResponseEntity.ok(email);
    }
}