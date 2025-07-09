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
        String nombreArchivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path ruta = Paths.get("uploads/" + nombreArchivo);

        // Crear carpeta si no existe
        Files.createDirectories(ruta.getParent());

        // Guardar archivo
        Files.write(ruta, file.getBytes());

        // Crear objeto Foto y guardar en base de datos
        Foto foto = Foto.builder()
                .titulo(titulo)
                .nombreArchivo(nombreArchivo)
                .url("/uploads/" + nombreArchivo)
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
