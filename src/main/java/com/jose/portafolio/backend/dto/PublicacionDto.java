package com.jose.portafolio.backend.dto;

import com.jose.portafolio.backend.model.Categoria;
import com.jose.portafolio.backend.model.Foto;
import com.jose.portafolio.backend.model.Usuario;
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

    @NotNull
    private Long categoriaId;

    @NotNull
    private Long usuarioId;

    private Long fotoId;

    // Para mostrar info completa en frontend
    private Categoria categoria;
    private Foto foto;
    private Usuario usuario;
}
