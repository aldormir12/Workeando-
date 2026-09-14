package com.workeando.plataform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.workeando.plataform.model.Proyecto;

public interface ProyectoService {
    Proyecto guardar(Proyecto proyecto);

    List<Proyecto> listarTodos();

    List<Proyecto> listarPorCategoriaYEstado(String nombreCategoria, String estado);

    Optional<Proyecto> buscarPorId(Long id);

    void eliminar(Long id);

    List<Proyecto> listarPorEstado(String estado);

    List<Proyecto> listarPorCreador(String correo);

    List<Proyecto> listarTodosPorCorreo(String correo);

    Page<Proyecto> listarTodosPorCorreoPaginado(String correo, Pageable pageable);

    Page<Proyecto> listarTodosPaginado(Pageable pageable);

    Page<Proyecto> listarPorEstadoPaginado(String estado, Pageable pageable);
}
