package com.jose.portafolio.backend.config;

import com.jose.portafolio.backend.model.Usuario;
import com.jose.portafolio.backend.model.Categoria;
import com.jose.portafolio.backend.model.Publicacion;
import com.jose.portafolio.backend.repository.UsuarioRepository;
import com.jose.portafolio.backend.repository.CategoriaRepository;
import com.jose.portafolio.backend.repository.PublicacionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@ConfigurationProperties(prefix = "admin")

public class DataInitializer {

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.nombre}")
    private String adminNombre;

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initData(
            UsuarioRepository usuarioRepo

    ) {
        return args -> {
            if (usuarioRepo.findByEmail(adminEmail).isEmpty()) {
                Usuario admin = Usuario.builder()
                        .nombre(adminNombre)
                        .email(adminEmail)
                        .password(new BCryptPasswordEncoder().encode(adminPassword))
                        .build();
                usuarioRepo.save(admin);
                System.out.println("✅ Usuario admin creado desde configuración.");
            }
        };
    }
}