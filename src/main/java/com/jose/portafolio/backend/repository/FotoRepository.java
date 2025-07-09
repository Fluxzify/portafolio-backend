package com.jose.portafolio.backend.repository;

import com.jose.portafolio.backend.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FotoRepository extends JpaRepository<Foto, Long> {
}