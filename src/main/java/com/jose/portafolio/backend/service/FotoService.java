package com.jose.portafolio.backend.service;

import com.jose.portafolio.backend.model.Foto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FotoService {
    Foto guardarFoto(MultipartFile archivo, String titulo) throws IOException;
    List<Foto> listarFotos();
    Optional<Foto> obtenerFotoPorId(Long id);
    void eliminarFoto(Long id) throws IOException;
    Foto guardarFotoUrl(String urlArchivo, String titulo) throws IOException;

}
