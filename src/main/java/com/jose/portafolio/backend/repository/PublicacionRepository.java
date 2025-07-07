
package com.jose.portafolio.backend.repository;

import com.jose.portafolio.backend.model.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface  PublicacionRepository extends JpaRepository<Publicacion, Long> {
}

