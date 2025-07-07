package com.jose.portafolio.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionDto {
    private Long id;

    @NotBlank
    private String titulo;

    @NotBlank
    private String urlFoto;

    @NotNull
    private Long categoriaId;

    @NotNull
    private Long usuarioId;
}
