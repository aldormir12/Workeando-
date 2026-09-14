package com.workeando.plataform.model;

import jakarta.persistence.*;

@Entity
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCalificacion;

    @ManyToOne
    @JoinColumn(name = "Contrato_idContrato")
    private Contrato contrato;

    private Integer calificador_id;
    private Integer calificado_id;
    private Integer puntuacion;
    private String comentario;

    public Integer getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(Integer idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Integer getCalificador_id() {
        return calificador_id;
    }

    public void setCalificador_id(Integer calificador_id) {
        this.calificador_id = calificador_id;
    }

    public Integer getCalificado_id() {
        return calificado_id;
    }

    public void setCalificado_id(Integer calificado_id) {
        this.calificado_id = calificado_id;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}