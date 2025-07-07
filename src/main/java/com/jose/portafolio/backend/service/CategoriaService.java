package com.jose.portafolio.backend.service;

import com.jose.portafolio.backend.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    Categoria crearCategoria(Categoria categoria);

    List<Categoria> listarCategorias();

    Optional<Categoria> obtenerCategoriaPorId(Long id);

    Categoria actualizarCategoria(Long id, Categoria categoriaActualizada);

    void eliminarCategoria(Long id);
}