package com.workeando.plataform.repository;

import com.workeando.plataform.model.Proyecto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    // Buscar todos los proyectos por estado
    List<Proyecto> findByEstado(String estado);

    // Buscar proyectos por creador (correo del empleador)
    List<Proyecto> findByCreadorCorreo(String correo);

    // Verificar si un proyecto existe por ID
    boolean existsById(@NonNull Long id);

    // Buscar proyectos que contengan el título
    List<Proyecto> findByTituloContaining(String titulo);

    // Método para encontrar proyectos por el nombre de la categoría y el estado
    List<Proyecto> findByCategoriaNombreIgnoreCaseAndEstadoIgnoreCase(String nombreCategoria, String estado);

    // Buscar proyectos por el ID de la categoría (agregado)
    List<Proyecto> findByCategoriaIdCategoria(Long categoriaId); // Este método buscará los proyectos por categoría ID

    // paginación
    Page<Proyecto> findByCreadorCorreo(String correo, Pageable pageable);

}
