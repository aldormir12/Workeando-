package com.workeando.plataform.service;

import com.workeando.plataform.model.Empleador;

import java.util.List;
import java.util.Optional;

public interface EmpleadorService {
    Empleador guardarEmpleador(Empleador empleador);
    List<Empleador> listarEmpleadores();
    Optional<Empleador> obtenerPorId(Integer id);
    void eliminarPorId(Integer id);
}
