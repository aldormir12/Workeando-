package com.workeando.plataform.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPago;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "id_contrato", nullable = false, unique = true)
    private Contrato contrato;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false, length = 10)
    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaConfirmacion;

    @Column(length = 100)
    private String referenciaExterna;

    @Column(length = 255)
    private String descripcion;

    // ====== CONSTRUCTOR CON VALORES POR DEFECTO ======
    public Pago() {
        this.estado = EstadoPago.PENDIENTE;
        this.metodoPago = MetodoPago.TRANSFERENCIA; // o el que quieras por defecto
        this.fechaCreacion = LocalDateTime.now();
        this.moneda = "PEN";
    }

    // ====== GETTERS / SETTERS ======

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public void setReferenciaExterna(String referenciaExterna) {
        this.referenciaExterna = referenciaExterna;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public enum EstadoPago {
        PENDIENTE,
        APROBADO,
        FALLIDO
    }

    public enum MetodoPago {
        TARJETA,
        TRANSFERENCIA,
        YAPE,
        PLIN,
        EFECTIVO,
        OTRO,
        SIMULADO   
    }
}
