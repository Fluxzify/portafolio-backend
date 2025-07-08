package com.jose.portafolio.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "Operaciones para subir archivos")
public class FotoController {

    @Operation(summary = "Subir un archivo", description = "Sube un archivo usando multipart/form-data")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> subirArchivo(
            @Parameter(
                    description = "Archivo a subir",
                    required = true,
                    in = ParameterIn.DEFAULT,
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacío");
        }

        String nombreArchivo = file.getOriginalFilename();
        Path ruta = Paths.get("uploads/" + nombreArchivo);

        try {
            Files.createDirectories(ruta.getParent());
            Files.write(ruta, file.getBytes());
            return ResponseEntity.ok("Archivo subido correctamente: " + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir archivo");
        }
    }
}
