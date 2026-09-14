
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
    boolean existsById(@NonNull Long id);  // Añadir la anotación @NonNull

    // Buscar proyectos que contengan el título
    List<Proyecto> findByTituloContaining(String titulo);

    //Filtrar proyectos
    List<Proyecto> findByCategoriaNombreIgnoreCaseAndEstadoIgnoreCase(String nombre, String estado);

    // NUEVOS MÉTODOS PARA PAGINACIÓN

    // Buscar proyectos por creador con paginación
    Page<Proyecto> findByCreadorCorreo(String correo, Pageable pageable);

    // Buscar proyectos por estado con paginación
    Page<Proyecto> findByEstado(String estado, Pageable pageable);

    // Buscar proyectos por categoría y estado con paginación
    Page<Proyecto> findByCategoriaNombreIgnoreCaseAndEstadoIgnoreCase(String nombre, String estado, Pageable pageable);

    // Buscar proyectos que contengan el título con paginación
    Page<Proyecto> findByTituloContaining(String titulo, Pageable pageable);
}

