package com.workeando.plataform.dto;

// DTO simple para enviar/recibir mensajes por STOMP
// Identificación por id de usuario; en UI se mostrará el nombre.
public class ChatMessageDTO {

    // Identificador del hilo: id de la postulación aceptada
    private Long postulacionId;

    // Remitente
    private Long remitenteId;
    private String remitenteNombre;

    // Destinatario
    private Long destinatarioId;

    // Contenido del mensaje
    private String contenido;

    // Momento del envío en milisegundos (epoch)
    private Long timestamp;

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(Long postulacionId, Long remitenteId, String remitenteNombre,
            Long destinatarioId, String contenido, Long timestamp) {
        this.postulacionId = postulacionId;
        this.remitenteId = remitenteId;
        this.remitenteNombre = remitenteNombre;
        this.destinatarioId = destinatarioId;
        this.contenido = contenido;
        this.timestamp = timestamp;
    }

    public Long getPostulacionId() {
        return postulacionId;
    }

    public void setPostulacionId(Long postulacionId) {
        this.postulacionId = postulacionId;
    }

    public Long getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(Long remitenteId) {
        this.remitenteId = remitenteId;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
