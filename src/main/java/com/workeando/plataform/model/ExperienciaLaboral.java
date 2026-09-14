package com.workeando.plataform.model;

import java.time.LocalDate;

import jakarta.persistence.Embeddable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Embeddable
public class ExperienciaLaboral {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String empresa;

    @NotBlank(message = "El puesto es obligatorio")
    private String puesto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500)
    private String descripcion;

    @NotBlank(message = "El período es obligatorio")
    private String periodo; // Ejemplo: "Enero 2020 - Marzo 2023"
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    // Getters y setters
    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

}
