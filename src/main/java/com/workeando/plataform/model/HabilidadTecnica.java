package com.workeando.plataform.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public class HabilidadTecnica {

    @NotBlank(message = "El nombre de la habilidad es obligatorio")
    private String nombre;

    @NotBlank(message = "El nivel de la habilidad es obligatorio")
    private String nivel;

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }
}
