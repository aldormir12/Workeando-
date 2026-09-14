package com.workeando.plataform.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "contrato")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContrato;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "idPostulacion", nullable = false)
    private Postulacion postulacion;

    @Column(name = "Fecha_Inicio")
    private LocalDate fechaInicio;

    @Column(name = "Fecha_Fin")
    private LocalDate fechaFin;

    @Column(length = 50)
    private String estado;

    @Column(length = 500)
    private String firmaEmpleadorImagenPath;

    @Column(length = 500)
    private String firmaFreelancerImagenPath;

    @JsonIgnore
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
    private List<Calificacion> calificaciones;

    @JsonIgnore
    @OneToOne(mappedBy = "contrato", cascade = CascadeType.ALL)
    private Pago pago;

    @Column(length = 150)
    private String tituloProyecto;

    @Column(length = 100)
    private String nombreEmpleador;

    @Column(length = 100)
    private String nombreFreelancer;

    @Lob
    private String cuerpoContrato;

    @Lob
    private String detallesAdicionales;

    private boolean enviado = false;

    private boolean firmadoEmpleador = false;

    private boolean firmadoFreelancer = false;

    private LocalDate fechaCreacion;
    private LocalDate fechaEnvio;
    private LocalDate fechaFirmaEmpleador;
    private LocalDate fechaFirmaFreelancer;

    @Lob
    private String firmaEmpleadorDigital;

    @Lob
    private String firmaFreelancerDigital;

    @Column(name = "pago_liberado")
    private Boolean pagoLiberado = false;

    @Column(name = "fecha_pago_liberado")
    private LocalDateTime fechaPagoLiberado;

    public Integer getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Integer idContrato) {
        this.idContrato = idContrato;
    }

    public Postulacion getPostulacion() {
        return postulacion;
    }

    public void setPostulacion(Postulacion postulacion) {
        this.postulacion = postulacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(List<Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public void setTituloProyecto(String tituloProyecto) {
        this.tituloProyecto = tituloProyecto;
    }

    public String getNombreEmpleador() {
        return nombreEmpleador;
    }

    public void setNombreEmpleador(String nombreEmpleador) {
        this.nombreEmpleador = nombreEmpleador;
    }

    public String getNombreFreelancer() {
        return nombreFreelancer;
    }

    public void setNombreFreelancer(String nombreFreelancer) {
        this.nombreFreelancer = nombreFreelancer;
    }

    public String getCuerpoContrato() {
        return cuerpoContrato;
    }

    public void setCuerpoContrato(String cuerpoContrato) {
        this.cuerpoContrato = cuerpoContrato;
    }

    public String getDetallesAdicionales() {
        return detallesAdicionales;
    }

    public void setDetallesAdicionales(String detallesAdicionales) {
        this.detallesAdicionales = detallesAdicionales;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }

    public boolean isFirmadoEmpleador() {
        return firmadoEmpleador;
    }

    public void setFirmadoEmpleador(boolean firmadoEmpleador) {
        this.firmadoEmpleador = firmadoEmpleador;
    }

    public boolean isFirmadoFreelancer() {
        return firmadoFreelancer;
    }

    public void setFirmadoFreelancer(boolean firmadoFreelancer) {
        this.firmadoFreelancer = firmadoFreelancer;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDate fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public LocalDate getFechaFirmaEmpleador() {
        return fechaFirmaEmpleador;
    }

    public void setFechaFirmaEmpleador(LocalDate fechaFirmaEmpleador) {
        this.fechaFirmaEmpleador = fechaFirmaEmpleador;
    }

    public LocalDate getFechaFirmaFreelancer() {
        return fechaFirmaFreelancer;
    }

    public void setFechaFirmaFreelancer(LocalDate fechaFirmaFreelancer) {
        this.fechaFirmaFreelancer = fechaFirmaFreelancer;
    }

    public String getFirmaEmpleadorDigital() {
        return firmaEmpleadorDigital;
    }

    public void setFirmaEmpleadorDigital(String firmaEmpleadorDigital) {
        this.firmaEmpleadorDigital = firmaEmpleadorDigital;
    }

    public String getFirmaFreelancerDigital() {
        return firmaFreelancerDigital;
    }

    public void setFirmaFreelancerDigital(String firmaFreelancerDigital) {
        this.firmaFreelancerDigital = firmaFreelancerDigital;
    }

    public String getFirmaEmpleadorImagenPath() {
        return firmaEmpleadorImagenPath;
    }

    public void setFirmaEmpleadorImagenPath(String firmaEmpleadorImagenPath) {
        this.firmaEmpleadorImagenPath = firmaEmpleadorImagenPath;
    }

    public String getFirmaFreelancerImagenPath() {
        return firmaFreelancerImagenPath;
    }

    public void setFirmaFreelancerImagenPath(String firmaFreelancerImagenPath) {
        this.firmaFreelancerImagenPath = firmaFreelancerImagenPath;
    }

    public void setPagoLiberado(boolean pagoLiberado) {
        this.pagoLiberado = pagoLiberado;
    }

    public LocalDateTime getFechaPagoLiberado() {
        return fechaPagoLiberado;
    }

    public void setFechaPagoLiberado(LocalDateTime fechaPagoLiberado) {
        this.fechaPagoLiberado = fechaPagoLiberado;
    }

    public Boolean getPagoLiberado() {
        return pagoLiberado;
    }

    public void setPagoLiberado(Boolean pagoLiberado) {
        this.pagoLiberado = pagoLiberado;
    }

    public boolean isPagoLiberado() {
        return Boolean.TRUE.equals(pagoLiberado);
    }
}
