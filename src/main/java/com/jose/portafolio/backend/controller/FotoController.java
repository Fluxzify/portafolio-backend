package com.jose.portafolio.backend.controller;

import com.jose.portafolio.backend.model.Foto;
import com.jose.portafolio.backend.service.FotoService;
import com.jose.portafolio.backend.service.SupabaseStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Tag(name = "Uploads", description = "Operaciones para subir archivos")
public class FotoController {

    private final SupabaseStorageService supabaseStorageService;
    private final FotoService fotoService;

    @Operation(
            summary = "Subir una imagen con título",
            description = "Sube el archivo al bucket de Supabase y crea una entrada en la base de datos",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "multipart/form-data")
            )
    )
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> subirFoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titulo") String titulo
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        try {
            // 1️⃣ Subir archivo a Supabase
            String urlArchivo = supabaseStorageService.uploadFile(file);

            // 2️⃣ Guardar info en la base de datos
            Foto foto = fotoService.guardarFotoUrl(urlArchivo, titulo);

            return ResponseEntity.ok(foto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir archivo");
        }
    }

    @Operation(
            summary = "Obtener una foto por su ID",
            description = "Retorna la información de una foto específica almacenada en la base de datos"
    )
    @GetMapping("/id/{id}")
    public ResponseEntity<?> obtenerFotoPorId(@PathVariable Long id) {
        Optional<Foto> optFoto = fotoService.obtenerFotoPorId(id);

        if (optFoto.isPresent()) {
            return ResponseEntity.ok(optFoto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Foto no encontrada con ID: " + id);
        }
    }
}
