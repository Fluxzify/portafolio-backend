package com.jose.portafolio.backend.service;

import com.jose.portafolio.backend.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario crearUsuario(Usuario usuario);

    List<Usuario> listarUsuarios();

    Optional<Usuario> obtenerUsuarioPorId(Long id);

    Usuario actualizarUsuario(Long id, Usuario usuarioActualizado);

    void eliminarUsuario(Long id);
}