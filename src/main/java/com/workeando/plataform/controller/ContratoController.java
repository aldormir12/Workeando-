package com.workeando.plataform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workeando.plataform.model.Contrato;
import com.workeando.plataform.service.ContratoService;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    @GetMapping
    public List<Contrato> listarContratos() {
        return contratoService.listarTodos();
    }

    @PostMapping
    public Contrato guardar(@RequestBody Contrato contrato) {
        return contratoService.guardar(contrato);
    }

    @GetMapping("/{id}")
    public Contrato obtener(@PathVariable Integer id) {
        return contratoService.buscarPorId(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        contratoService.eliminar(id);
    }
}
