
package com.workeando.plataform.service.impl;

import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.repository.ProyectoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProyectoServiceImpl {

    private final ProyectoRepository proyectoRepository;

    public ProyectoServiceImpl(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    // Guardar o actualizar un proyecto
    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    // Listar todos los proyectos
    public List<Proyecto> listarTodos() {
        return proyectoRepository.findAll();
    }

    // Filtrar proyectos por categoria
    public List<Proyecto> listarPorCategoriaYEstado(String nombreCategoria, String estado) {
        return proyectoRepository.findByCategoriaNombreIgnoreCaseAndEstadoIgnoreCase(nombreCategoria, estado);
    }

    // Buscar por ID
    public Optional<Proyecto> buscarPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    // Eliminar por ID
    public void eliminar(Long id) {
        // Verificar si el proyecto existe antes de eliminarlo
        if (proyectoRepository.existsById(id)) {
            proyectoRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Proyecto con ID " + id + " no existe.");
        }
    }

    // Buscar proyectos por estado
    public List<Proyecto> listarPorEstado(String estado) {
        return proyectoRepository.findByEstado(estado);
    }

    // Buscar proyectos por creador
    public List<Proyecto> listarPorCreador(String correo) {
        return proyectoRepository.findByCreadorCorreo(correo);
    }

    // Método con misma lógica que listarPorCreador()
    public List<Proyecto> listarTodosPorCorreo(String correo) {
        return proyectoRepository.findByCreadorCorreo(correo);
    }

    // NUEVOS MÉTODOS PARA PAGINACIÓN

    // Listar proyectos por correo con paginación
    public Page<Proyecto> listarTodosPorCorreoPaginado(String correo, Pageable pageable) {
        return proyectoRepository.findByCreadorCorreo(correo, pageable);
    }

    // Listar todos los proyectos con paginación
    public Page<Proyecto> listarTodosPaginado(Pageable pageable) {
        return proyectoRepository.findAll(pageable);
    }

    // Listar proyectos por estado con paginación
    public Page<Proyecto> listarPorEstadoPaginado(String estado, Pageable pageable) {
        return proyectoRepository.findByEstado(estado, pageable);
    }
}