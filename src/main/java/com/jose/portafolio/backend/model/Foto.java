package com.jose.portafolio.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fotos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String nombreArchivo;

    private String url; // ej: /uploads/imagen.jpg
}
