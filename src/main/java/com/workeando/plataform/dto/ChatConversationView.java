package com.workeando.plataform.dto;

import java.time.LocalDateTime;

public class ChatConversationView {

    private Long postulacionId;
    private String proyectoTitulo;

    private Long otroUsuarioId;
    private String otroUsuarioNombre;

    private String ultimoMensaje;
    private LocalDateTime ultimaFecha;

    // getters y setters
    public Long getPostulacionId() { return postulacionId; }
    public void setPostulacionId(Long postulacionId) { this.postulacionId = postulacionId; }

    public String getProyectoTitulo() { return proyectoTitulo; }
    public void setProyectoTitulo(String proyectoTitulo) { this.proyectoTitulo = proyectoTitulo; }

    public Long getOtroUsuarioId() { return otroUsuarioId; }
    public void setOtroUsuarioId(Long otroUsuarioId) { this.otroUsuarioId = otroUsuarioId; }

    public String getOtroUsuarioNombre() { return otroUsuarioNombre; }
    public void setOtroUsuarioNombre(String otroUsuarioNombre) { this.otroUsuarioNombre = otroUsuarioNombre; }

    public String getUltimoMensaje() { return ultimoMensaje; }
    public void setUltimoMensaje(String ultimoMensaje) { this.ultimoMensaje = ultimoMensaje; }

    public LocalDateTime getUltimaFecha() { return ultimaFecha; }
    public void setUltimaFecha(LocalDateTime ultimaFecha) { this.ultimaFecha = ultimaFecha; }
}
