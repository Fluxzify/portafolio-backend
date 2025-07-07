package com.jose.portafolio.backend.serviceImpl;

import com.jose.portafolio.backend.model.Publicacion;
import com.jose.portafolio.backend.repository.PublicacionRepository;
import com.jose.portafolio.backend.service.PublicacionService;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

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

        publicacion.setUrlFoto(publicacionActualizada.getUrlFoto());
        publicacion.setCategoria(publicacionActualizada.getCategoria());
        publicacion.setTitulo(publicacionActualizada.getTitulo());


        return publicacionRepository.save(publicacion);
    }

    @Override
    public void eliminarPublicacion(Long id) {
        publicacionRepository.deleteById(id);
    }
}
