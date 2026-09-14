package com.workeando.plataform.repository;

//import com.workeando.plataform.model.Freelancer;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workeando.plataform.model.Postulacion;

@Repository
public interface PostulacionRepository extends JpaRepository<Postulacion, Long> {

    // Obtener todas las postulaciones de un proyecto
    List<Postulacion> findByProyectoId(Long proyectoId);

    // Evitar postulaciones duplicadas por el mismo freelancer al mismo proyecto
    boolean existsByProyectoIdAndCorreoFreelancer(Long proyectoId, String correoFreelancer);

    // obtener postulaciones por correo
    List<Postulacion> findByCorreoFreelancer(String correoFreelancer);

    List<Postulacion> findByProyectoCreadorCorreo(String correo);

    Page<Postulacion> findByProyectoCreadorCorreo(String correo, Pageable pageable);

}
