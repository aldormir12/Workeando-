package com.workeando.plataform.service;

import com.workeando.plataform.model.Proyecto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface ProyectoService {

    Proyecto guardar(Proyecto proyecto);

    List<Proyecto> listarTodos();

    List<Proyecto> listarPorCategoriaYEstado(String nombreCategoria, String estado);

    Optional<Proyecto> buscarPorId(Long id);

    void eliminar(Long id);

    List<Proyecto> listarPorEstado(String estado);

    List<Proyecto> listarPorCreador(String correo);

    List<Proyecto> listarTodosPorCorreo(String correo);

// Declaración del método para listar proyectos por categoría
    List<Proyecto> listarPorCategoria(Long categoriaId);


    // paginación
    Page<Proyecto> listarPorCorreoPaginado(String correo, Pageable pageable);
    

}
