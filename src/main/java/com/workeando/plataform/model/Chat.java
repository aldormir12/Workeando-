package com.workeando.plataform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMensaje;

    @ManyToOne
    @JoinColumn(name = "Usuario_idUsuario")
    private Usuario usuario;

    private Integer remitente_id;
    private Integer destinatario_id;
    private String contenido;
    private LocalDateTime fechaEnvio;

    public Integer getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(Integer idMensaje) {
        this.idMensaje = idMensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getRemitente_id() {
        return remitente_id;
    }

    public void setRemitente_id(Integer remitente_id) {
        this.remitente_id = remitente_id;
    }

    public Integer getDestinatario_id() {
        return destinatario_id;
    }

    public void setDestinatario_id(Integer destinatario_id) {
        this.destinatario_id = destinatario_id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}