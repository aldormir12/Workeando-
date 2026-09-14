package com.workeando.plataform.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Embeddable
public class ExperienciaLaboral {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String empresa;

    @NotBlank(message = "El puesto es obligatorio")
    private String puesto;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
    private String descripcion;

    @NotBlank(message = "La fecha de inicio es obligatoria")
    @Pattern(regexp = "^\\d{2}/\\d{4}$", message = "La fecha de inicio debe tener el formato MM/YYYY")
    private String fechaDesde;

    @NotBlank(message = "La fecha de fin es obligatoria")
    @Pattern(regexp = "^(\\d{2}/\\d{4}|Actualidad)$", message = "La fecha de fin debe tener el formato MM/YYYY o ser 'Actualidad'")
    private String fechaHasta;

    // Getters y Setters

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

    public String getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(String fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public String getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(String fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
}
