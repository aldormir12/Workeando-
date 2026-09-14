package com.workeando.plataform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workeando.plataform.model.Calificacion;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    // Calificaciones que ha recibido un FREELANCER
    List<Calificacion> findByContrato_Postulacion_Freelancer_Usuario_Id(Long usuarioId);

    // Calificaciones que ha recibido un EMPLEADOR
    List<Calificacion> findByContrato_Postulacion_Proyecto_CreadorCorreo(String creadorCorreo);

    // Para evitar que el mismo usuario califique dos veces el mismo contrato
    boolean existsByContrato_IdContratoAndCalificadorId(Integer idContrato, Integer calificadorId);
}
