package com.workeando.plataform.service;

import java.util.List;
import java.util.Optional;

import com.workeando.plataform.model.Contrato;

public interface ContratoService {
    List<Contrato> listarTodos();
    Optional<Contrato> buscarPorId(Integer id);
    Contrato guardar(Contrato contrato);
    void eliminar(Integer id);
}

