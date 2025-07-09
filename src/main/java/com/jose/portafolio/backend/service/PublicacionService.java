package com.jose.portafolio.backend.service;

import com.jose.portafolio.backend.model.Publicacion;

import java.util.List;
import java.util.Optional;

public interface PublicacionService {
    Publicacion crearPublicacion(Publicacion publicacion);

    List<Publicacion> listarPublicaciones();

    Optional<Publicacion> obtenerPublicacionPorId(Long id);

    Publicacion actualizarPublicacion(Long id, Publicacion publicacionActualizada);

    void eliminarPublicacion(Long id);
    List<Publicacion> buscarPorCategoria(String nombreCategoria);

}
