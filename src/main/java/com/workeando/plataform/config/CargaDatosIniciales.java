package com.workeando.plataform.config;

import com.workeando.plataform.repository.CategoriaRepository;
import com.workeando.plataform.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.List;

@Component
public class CargaDatosIniciales {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CargaDatosIniciales(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void cargarCategoriasIniciales() {
        if (categoriaRepository.count() == 0) {
            List<Categoria> categorias = Arrays.asList(
                new Categoria("DISEÑO"),
                new Categoria("COCINA_GASTRONOMIA"),
                new Categoria("MARKETING"),
                new Categoria("REDACCION"),
                new Categoria("TRADUCCION"),
                new Categoria("ANIMALES"),
                new Categoria("SERVICIOS_GENERALES"),
                new Categoria("CREATIVOS_ARTE"),
                new Categoria("PARA_TURISTAS"),
                new Categoria("LOGISTICA_OPERACIONES"),
                new Categoria("EDUCACION_TUTORIAS"),
                new Categoria("TECNOLOGIA_DESARROLLO")
            );
            categoriaRepository.saveAll(categorias);
            System.out.println("✔ Categorías iniciales insertadas correctamente.");
        } else {
            System.out.println("ℹ Las categorías ya existen. No se insertó nada.");
        }
    }
}
