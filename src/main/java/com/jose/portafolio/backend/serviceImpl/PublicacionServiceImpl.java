package com.jose.portafolio.backend.serviceImpl;

import com.jose.portafolio.backend.model.Publicacion;
import com.jose.portafolio.backend.repository.PublicacionRepository;
import com.jose.portafolio.backend.service.PublicacionService;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PublicacionServiceImpl implements PublicacionService {

    private final PublicacionRepository publicacionRepository;

    public PublicacionServiceImpl(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    @Override
    public Publicacion crearPublicacion(Publicacion publicacion) {
        return publicacionRepository.save(publicacion);
    }

    @Override
    public List<Publicacion> listarPublicaciones() {
        return publicacionRepository.findAll();
    }

    @Override
    public Optional<Publicacion> obtenerPublicacionPorId(Long id) {
        return publicacionRepository.findById(id);
    }

    @Override
    public Publicacion actualizarPublicacion(Long id, Publicacion publicacionActualizada) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));

        publicacion.setTitulo(publicacionActualizada.getTitulo());
        publicacion.setCategoria(publicacionActualizada.getCategoria());
        publicacion.setFoto(publicacionActualizada.getFoto()); // ahora es entidad

        return publicacionRepository.save(publicacion);
    }

    @Override
    public void eliminarPublicacion(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Publicación no encontrada"));


        if (publicacion.getFoto() != null) {
            String nombreArchivo = publicacion.getFoto().getNombreArchivo();
            if (nombreArchivo != null && !nombreArchivo.isBlank()) {
                Path ruta = Paths.get("uploads/" + nombreArchivo);
                try {
                    Files.deleteIfExists(ruta);
                } catch (IOException e) {
                    System.err.println("⚠️ No se pudo eliminar la imagen física: " + nombreArchivo);
                }
            }
        }

        publicacionRepository.delete(publicacion);
    }

    @Override
    public List<Publicacion> buscarPorCategoria(String nombreCategoria) {
        return publicacionRepository.findByCategoriaNombre(nombreCategoria);
    }
}
