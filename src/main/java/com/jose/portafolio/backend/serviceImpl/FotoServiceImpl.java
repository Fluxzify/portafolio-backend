package com.jose.portafolio.backend.serviceImpl;

import com.jose.portafolio.backend.model.Foto;
import com.jose.portafolio.backend.repository.FotoRepository;
import com.jose.portafolio.backend.service.FotoService;
import com.jose.portafolio.backend.service.SupabaseStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FotoServiceImpl implements FotoService {

    private final FotoRepository fotoRepository;
    private final SupabaseStorageService supabaseStorageService; // inyectado

    @Override
    public Foto guardarFoto(MultipartFile file, String titulo) throws IOException {
        // Validar tipo MIME
        String tipo = file.getContentType();
        if (tipo == null || !List.of("image/jpeg", "image/png", "image/webp").contains(tipo)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + tipo);
        }

        // Validar extensión segura
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .map(nombre -> nombre.substring(nombre.lastIndexOf('.') + 1))
                .orElse("");
        if (!List.of("jpg", "jpeg", "png", "webp").contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Extensión de archivo no permitida: " + extension);
        }

        // Subir archivo a Supabase y obtener la URL pública
        String urlArchivo = supabaseStorageService.uploadFile(file);

        // Crear objeto Foto y guardar en base de datos
        Foto foto = Foto.builder()
                .titulo(titulo)
                .nombreArchivo(file.getOriginalFilename())
                .url(urlArchivo) // <-- aquí guardamos la URL del bucket
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
    public void eliminarFoto(Long id) throws IOException {
        Optional<Foto> optFoto = fotoRepository.findById(id);
        if (optFoto.isPresent()) {
            Foto foto = optFoto.get();
            // Eliminar archivo de Supabase
            supabaseStorageService.deleteFile(foto.getNombreArchivo());
            // Eliminar registro de la base de datos
            fotoRepository.deleteById(id);
        }
    }
    @Override
    public Foto guardarFotoUrl(String urlArchivo, String titulo) throws IOException {
        // Guardamos directamente la URL en la base de datos
        Foto foto = Foto.builder()
                .titulo(titulo)
                .nombreArchivo(urlArchivo.substring(urlArchivo.lastIndexOf('/') + 1)) // extrae el nombre
                .url(urlArchivo)
                .build();

        return fotoRepository.save(foto);
    }
}
