package com.workeando.plataform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workeando.plataform.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    Optional<Pago> findByContrato_IdContrato(Integer idContrato);

    boolean existsByContrato_IdContrato(Integer idContrato);
}
