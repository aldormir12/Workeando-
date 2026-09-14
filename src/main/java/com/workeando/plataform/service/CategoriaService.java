package com.workeando.plataform.service;

import com.workeando.plataform.model.Categoria;
import com.workeando.plataform.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;


import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Retorna todas las categorías registradas en la base de datos.
     */
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    /**
     * Busca una categoría por su nombre (ignorando mayúsculas/minúsculas).
     */
    public Optional<Categoria> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre);
    }

    /**
     * Guarda una nueva categoría en la base de datos.
     */
    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

}
