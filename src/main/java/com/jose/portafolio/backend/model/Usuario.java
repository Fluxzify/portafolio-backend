package com.jose.portafolio.backend.model;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Roles rol;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email
    @Column(unique = true)
    private String email;

    private String password;
}