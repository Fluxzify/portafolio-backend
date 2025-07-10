package com.jose.portafolio.backend.controller;

import com.jose.portafolio.backend.dto.PublicacionDto;
import com.jose.portafolio.backend.model.Categoria;
import com.jose.portafolio.backend.model.Foto;
import com.jose.portafolio.backend.model.Publicacion;
import com.jose.portafolio.backend.model.Usuario;
import com.jose.portafolio.backend.service.CategoriaService;
import com.jose.portafolio.backend.service.FotoService;
import com.jose.portafolio.backend.service.PublicacionService;
import com.jose.portafolio.backend.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publicaciones")
public class PublicacionController {

    private final PublicacionService publicacionService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final FotoService fotoService;

    public PublicacionController(PublicacionService publicacionService,
                                 CategoriaService categoriaService,
                                 UsuarioService usuarioService,
                                 FotoService fotoService) {
        this.publicacionService = publicacionService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
        this.fotoService = fotoService;
    }

    @GetMapping
    public ResponseEntity<List<PublicacionDto>> listarPublicaciones() {
        List<PublicacionDto> publicaciones = publicacionService.listarPublicaciones()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(publicaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacionDto> obtenerPublicacionPorId(@PathVariable Long id) {
        Publicacion publicacion = publicacionService.obtenerPublicacionPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicación no encontrada"));

        return ResponseEntity.ok(convertirADto(publicacion));
    }

    @PostMapping
    public ResponseEntity<?> crearPublicacion(@RequestBody PublicacionDto dto) {
        try {
            Publicacion publicacion = convertirAEntidad(dto);
            Publicacion guardada = publicacionService.crearPublicacion(publicacion);
            return ResponseEntity.status(201).body(Map.of(
                    "message", "Publicación creada correctamente",
                    "data", convertirADto(guardada)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPublicacion(@PathVariable Long id, @RequestBody PublicacionDto dto) {
        try {
            Publicacion actualizada = publicacionService.actualizarPublicacion(id, convertirAEntidad(dto));
            return ResponseEntity.ok(Map.of(
                    "message", "Publicación actualizada correctamente",
                    "data", convertirADto(actualizada)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPublicacion(@PathVariable Long id) {
        try {
            publicacionService.eliminarPublicacion(id);
            return ResponseEntity.ok(Map.of("message", "Publicación eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "No se pudo eliminar: " + e.getMessage()));
        }
    }

    @GetMapping("/categoria/{nombre}")
    public ResponseEntity<?> buscarPorCategoria(@PathVariable String nombre) {
        List<Publicacion> publicaciones = publicacionService.buscarPorCategoria(nombre);
        if (publicaciones.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "No se encontraron publicaciones en esa categoría"));
        }
        List<PublicacionDto> dtos = publicaciones.stream().map(this::convertirADto).toList();
        return ResponseEntity.ok(dtos);
    }

    private PublicacionDto convertirADto(Publicacion publicacion) {
        return PublicacionDto.builder()
                .id(publicacion.getId())
                .titulo(publicacion.getTitulo())
                .categoriaId(publicacion.getCategoria().getId())
                .usuarioId(publicacion.getUsuario().getId())
                .fotoId(publicacion.getFoto() != null ? publicacion.getFoto().getId() : null)
                .categoria(publicacion.getCategoria())
                .usuario(publicacion.getUsuario())
                .foto(publicacion.getFoto())
                .build();
    }

    private Publicacion convertirAEntidad(PublicacionDto dto) {
        Categoria categoria = categoriaService.obtenerCategoriaPorId(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Usuario usuario = usuarioService.obtenerUsuarioPorId(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Foto foto = null;
        if (dto.getFotoId() != null) {
            foto = fotoService.obtenerFotoPorId(dto.getFotoId())
                    .orElseThrow(() -> new RuntimeException("Foto no encontrada"));
        }

        return Publicacion.builder()
                .id(dto.getId())
                .titulo(dto.getTitulo())
                .categoria(categoria)
                .usuario(usuario)
                .foto(foto)
                .build();
    }
}
