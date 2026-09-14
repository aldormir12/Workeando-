package com.workeando.plataform.serviceimpl;

import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.repository.ProyectoRepository;
import com.workeando.plataform.service.ProyectoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProyectoServiceImpl implements ProyectoService{
    @Autowired

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

    // Método con misma lógica que listarPorCreador(),

    public List<Proyecto> listarTodosPorCorreo(String correo) {
        return proyectoRepository.findByCreadorCorreo(correo);
    }
    // paginación
 @Override
    public Page<Proyecto> listarPorCorreoPaginado(String correo, Pageable pageable) {
        return proyectoRepository.findByCreadorCorreo(correo, pageable);
    }


    // Filtrar proyectos por categoría (usando el ID de la categoría)
    @Override
    public List<Proyecto> listarPorCategoria(Long categoriaId) {
        return proyectoRepository.findByCategoriaIdCategoria(categoriaId);  // Llamamos al método del repositorio
    }
}
