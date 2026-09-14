package com.workeando.plataform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workeando.plataform.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}
