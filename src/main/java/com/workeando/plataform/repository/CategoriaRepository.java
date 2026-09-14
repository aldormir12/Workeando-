package com.workeando.plataform.repository;

import com.workeando.plataform.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Busca una categoría por nombre, ignorando mayúsculas y minúsculas.
     */
    Optional<Categoria> findByNombreIgnoreCase(String nombre);
}

