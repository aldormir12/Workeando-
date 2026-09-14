package com.workeando.plataform.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;

@Entity
@Table(name = "postulaciones")
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // almacena el nombre y el correo del postulante
    private String nombreFreelancer;
    private String correoFreelancer;

    private Double montoPropuesto; // puede ser null

    private LocalDate fechaPostulacion = LocalDate.now();

    // relacion en la base de datos con la entidad proyecto
    @ManyToOne // postulaciones - proyecto
    @JoinColumn(name = "proyecto_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    @Column(nullable = false)
    private String estado = "Pendiente";

    @Column(nullable = false)
    private Boolean cvVisto = false;

    @Column(nullable = false)
    private Boolean finalista = false;

    private LocalDateTime cvVistoAt;

    // constructor
    public Postulacion() {
    }

    public Postulacion(String nombreFreelancer, String correoFreelancer, Double montoPropuesto, Proyecto proyecto) {
        this.nombreFreelancer = nombreFreelancer;
        this.correoFreelancer = correoFreelancer;
        this.montoPropuesto = montoPropuesto;
        this.proyecto = proyecto;
        this.fechaPostulacion = LocalDate.now();
    }

    // Getters y setters

    public Freelancer getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(Freelancer freelancer) {
        this.freelancer = freelancer;
    }

    public Long getId() {
        return id;
    }

    public String getNombreFreelancer() {
        return nombreFreelancer;
    }

    public void setNombreFreelancer(String nombreFreelancer) {
        this.nombreFreelancer = nombreFreelancer;
    }

    public String getCorreoFreelancer() {
        return correoFreelancer;
    }

    public void setCorreoFreelancer(String correoFreelancer) {
        this.correoFreelancer = correoFreelancer;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getMontoPropuesto() {
        return montoPropuesto;
    }

    public void setMontoPropuesto(Double montoPropuesto) {
        this.montoPropuesto = montoPropuesto;
    }

    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(LocalDate fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Boolean getCvVisto() {
        return cvVisto;
    }

    public void setCvVisto(Boolean cvVisto) {
        this.cvVisto = cvVisto;
    }

    public Boolean getFinalista() {
        return finalista;
    }

    public void setFinalista(Boolean finalista) {
        this.finalista = finalista;
    }

    public LocalDateTime getCvVistoAt() {
        return cvVistoAt;
    }

    public void setCvVistoAt(LocalDateTime cvVistoAt) {
        this.cvVistoAt = cvVistoAt;
    }
}
