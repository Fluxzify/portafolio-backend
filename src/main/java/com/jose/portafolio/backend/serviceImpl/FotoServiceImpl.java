package com.jose.portafolio.backend.serviceImpl;

import com.jose.portafolio.backend.model.Foto;
import com.jose.portafolio.backend.repository.FotoRepository;
import com.jose.portafolio.backend.service.FotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FotoServiceImpl implements FotoService {

    private final FotoRepository fotoRepository;

    private final String UPLOAD_DIR = "uploads/";

    @Override
    public Foto guardarFoto(MultipartFile file, String titulo) throws IOException {
        // Validar tipo MIME
        String tipo = file.getContentType();
        if (tipo == null || !List.of("image/jpeg", "image/png", "image/webp").contains(tipo)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + tipo);
        }

        // Obtener extensión segura
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .map(nombre -> nombre.substring(nombre.lastIndexOf('.') + 1))
                .orElse("");

        // Validar extensión
        if (!List.of("jpg", "jpeg", "png", "webp").contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Extensión de archivo no permitida: " + extension);
        }

        // Generar nombre único seguro (UUID)
        String nombreArchivoSeguro = java.util.UUID.randomUUID().toString() + "." + extension;

        // Crear carpeta si no existe
        Path carpetaUploads = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        Files.createDirectories(carpetaUploads);

        // Ruta segura
        Path rutaDestino = carpetaUploads.resolve(nombreArchivoSeguro).normalize();

        // Evitar path traversal
        if (!rutaDestino.startsWith(carpetaUploads)) {
            throw new SecurityException("Ruta inválida detectada");
        }

        // Guardar archivo
        Files.write(rutaDestino, file.getBytes());

        // Crear objeto Foto y guardar en base de datos
        Foto foto = Foto.builder()
                .titulo(titulo)
                .nombreArchivo(nombreArchivoSeguro)
                .url("/uploads/" + nombreArchivoSeguro)
                .build();

        return fotoRepository.save(foto);
    }

    @Override
    public List<Foto> listarFotos() {
        return fotoRepository.findAll();
    }

    @Override
    public Optional<Foto> obtenerFotoPorId(Long id) {
        return fotoRepository.findById(id);
    }

    @Override
    public void eliminarFoto(Long id) {
        fotoRepository.deleteById(id);
    }
}
