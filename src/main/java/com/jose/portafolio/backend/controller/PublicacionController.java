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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    // GET /api/publicaciones
    @GetMapping
    public ResponseEntity<List<PublicacionDto>> listarPublicaciones() {
        List<PublicacionDto> publicaciones = publicacionService.listarPublicaciones()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(publicaciones);
    }

    // GET /api/publicaciones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PublicacionDto> obtenerPublicacionPorId(@PathVariable Long id) {
        Optional<Publicacion> publicacion = publicacionService.obtenerPublicacionPorId(id);
        return publicacion.map(p -> ResponseEntity.ok(convertirADto(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/publicaciones
    @PostMapping
    public ResponseEntity<PublicacionDto> crearPublicacion(@RequestBody PublicacionDto dto) {
        Publicacion publicacion = convertirAEntidad(dto);
        Publicacion guardada = publicacionService.crearPublicacion(publicacion);
        return ResponseEntity.ok(convertirADto(guardada));
    }

    // PUT /api/publicaciones/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PublicacionDto> actualizarPublicacion(@PathVariable Long id, @RequestBody PublicacionDto dto) {
        Publicacion actualizada = publicacionService.actualizarPublicacion(id, convertirAEntidad(dto));
        return ResponseEntity.ok(convertirADto(actualizada));
    }

    // DELETE /api/publicaciones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPublicacion(@PathVariable Long id) {
        publicacionService.eliminarPublicacion(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/publicaciones/categoria/{nombre}
    @GetMapping("/categoria/{nombre}")
    public ResponseEntity<List<Publicacion>> buscarPorCategoria(@PathVariable String nombre) {
        return ResponseEntity.ok(publicacionService.buscarPorCategoria(nombre));
    }

    // 🔁 Conversión Entidad -> DTO
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


    // 🔁 Conversión DTO -> Entidad
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
