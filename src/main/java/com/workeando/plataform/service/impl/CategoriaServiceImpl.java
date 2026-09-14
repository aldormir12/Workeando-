package com.workeando.plataform.service.impl;

import com.workeando.plataform.model.Categoria;
import com.workeando.plataform.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl {
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }
}
