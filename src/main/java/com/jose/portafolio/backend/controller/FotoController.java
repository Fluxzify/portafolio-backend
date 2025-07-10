package com.jose.portafolio.backend.controller;

import com.jose.portafolio.backend.model.Foto;
import com.jose.portafolio.backend.service.FotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Tag(name = "Uploads", description = "Operaciones para subir archivos")
public class FotoController {

    private final FotoService fotoService;

    @Operation(
            summary = "Subir una imagen con título",
            description = "Guarda el archivo en el servidor y crea una entrada en la base de datos",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subirFoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titulo") String titulo
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        try {
            Foto foto = fotoService.guardarFoto(file, titulo);
            return ResponseEntity.ok(foto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir archivo");
        }
    }

    @GetMapping("/{nombreArchivo:.+}")
    @Operation(
            summary = "Ver o descargar una imagen",
            description = "Retorna el archivo por su nombre, con el tipo MIME correcto"
    )
    public ResponseEntity<Resource> verFoto(
            @Parameter(name = "nombreArchivo", description = "Nombre único del archivo", in = ParameterIn.PATH)
            @PathVariable String nombreArchivo
    ) {
        try {
            // Carpeta de almacenamiento
            Path carpetaBase = Paths.get("uploads").toAbsolutePath().normalize();
            Path rutaArchivo = carpetaBase.resolve(nombreArchivo).normalize();

            // Protección contra path traversal
            if (!rutaArchivo.startsWith(carpetaBase)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Resource recurso = new UrlResource(rutaArchivo.toUri());

            if (!recurso.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Detectar el tipo MIME
            String tipoMime = Files.probeContentType(rutaArchivo);
            if (tipoMime == null) {
                tipoMime = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(tipoMime))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .body(recurso);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
