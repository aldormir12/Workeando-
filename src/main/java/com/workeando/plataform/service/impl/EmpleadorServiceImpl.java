package com.workeando.plataform.service.impl;

import com.workeando.plataform.model.Empleador;
import com.workeando.plataform.repository.EmpleadorRepository;
import com.workeando.plataform.service.EmpleadorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadorServiceImpl implements EmpleadorService {

    @Autowired
    private EmpleadorRepository empleadorRepository;

    @Override
    public Empleador guardarEmpleador(Empleador empleador) {
        return empleadorRepository.save(empleador);
    }

    @Override
    public List<Empleador> listarEmpleadores() {
        return empleadorRepository.findAll();
    }

    @Override
    public Optional<Empleador> obtenerPorId(Integer id) {
        return empleadorRepository.findById(id);
    }

    @Override
    public void eliminarPorId(Integer id) {
        empleadorRepository.deleteById(id);
    }
}
