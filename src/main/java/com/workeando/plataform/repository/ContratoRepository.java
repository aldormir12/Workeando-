package com.workeando.plataform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workeando.plataform.model.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {
}
