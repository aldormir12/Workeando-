package com.workeando.plataform.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workeando.plataform.model.Contrato;
import com.workeando.plataform.repository.ContratoRepository;
import com.workeando.plataform.service.ContratoService;

@Service
public class ContratoServiceImpl implements ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Override
    public List<Contrato> listarTodos() {
        return contratoRepository.findAll();
    }

    @Override
    public Optional<Contrato> buscarPorId(Integer id) {
        return contratoRepository.findById(id);
    }

    @Override
    public Contrato guardar(Contrato contrato) {
        return contratoRepository.save(contrato);
    }

    @Override
    public void eliminar(Integer id) {
        contratoRepository.deleteById(id);
    }
}
