package com.workeando.plataform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import jakarta.persistence.EnumType;

@Embeddable
public class Idioma implements Serializable {

    @NotBlank(message = "El nombre del idioma no puede estar vacío")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nivel nivel;

   public enum Nivel {
    A1_PRINCIPIANTE("A1 - Principiante"),
    A2_BASICO("A2 - Básico"),
    B1_INTERMEDIO_BAJO("B1 - Intermedio bajo"),
    B2_INTERMEDIO_ALTO("B2 - Intermedio alto"),
    C1_AVANZADO("C1 - Avanzado"),
    C2_EXPERTO("C2 - Experto"),
    BASICO("Básico"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado"),
    NATIVO("Nativo");

    private final String etiqueta;

    Nivel(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}


    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

}
