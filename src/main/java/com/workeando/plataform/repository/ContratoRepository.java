package com.workeando.plataform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workeando.plataform.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    // Buscar contrato por el id de la postulacion
    Optional<Contrato> findByPostulacionId(Long postulacionId);
}
