package com.workeando.plataform.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100, message = "El título no debe superar 100 caracteres")
    private String titulo;

    @Column(length = 3000)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "fecha_publicacion", nullable = false, updatable = false)
    private LocalDateTime fechaPublicacion = LocalDateTime.now();

    @NotNull(message = "El presupuesto es obligatorio")
    @DecimalMin(value = "10.0", message = "El presupuesto no puede ser menor a 10")
    @DecimalMax(value = "50000.0", message = "El presupuesto no puede exceder los 50,000")
    private double presupuesto;
    // modalidad de pago
    @Enumerated(EnumType.STRING)
    private ModalidadPago modalidadPago;

    @Column(name = "estado", nullable = false)
    private String estado = "Abierto"; // abierto, cerrado

    private String creadorCorreo; // correo del empleador que publicó el proyecto

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(min = 5, max = 100, message = "La ubicación debe tener entre 5 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZÁÉÍÓÚáéíóúñÑ0-9\\s,\\.\\-#]+$", message = "La ubicación solo puede contener letras, números y signos comunes")
    private String ubicacion; // Ubicación del proyecto

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
    private LocalDate fechaInicio; // Fecha de inicio

    @NotNull(message = "La fecha de finalización es obligatoria")
    private LocalDate fechaFinal; // Fecha final

    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad; // Modalidad del trabajo

    // Campos formateados para mostrar en la vista
    @Transient
    private String fechaInicioFormateada;

    @Transient
    private String fechaFinalFormateada;

    @ManyToOne
    @JoinColumn(name = "idEmpleador")
    private Empleador empleador;

    @PrePersist
    @PreUpdate
    private void syncEstados() {
        if (estadoProyecto == null) {
            estadoProyecto = EstadoProyecto.PUBLICADO;
        }
        if (estadoProyecto == EstadoProyecto.PUBLICADO) {
            estado = "Abierto";
        } else {
            estado = "Cerrado";
        }
    }

    public Proyecto() {
    }

    public Proyecto(String titulo, String descripcion, Categoria categoria, double presupuesto,
            String creadorCorreo, String ubicacion, LocalDate fechaInicio, LocalDate fechaFinal, String modalidad) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.presupuesto = presupuesto;
        this.creadorCorreo = creadorCorreo;
        this.ubicacion = ubicacion;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.modalidad = modalidad;
    }

    // Getters y setters para campos formateados
    public String getFechaInicioFormateada() {
        return fechaInicioFormateada;
    }

    public void setFechaInicioFormateada(String fechaInicioFormateada) {
        this.fechaInicioFormateada = fechaInicioFormateada;
    }

    public String getFechaFinalFormateada() {
        return fechaFinalFormateada;
    }

    public void setFechaFinalFormateada(String fechaFinalFormateada) {
        this.fechaFinalFormateada = fechaFinalFormateada;
    }

    // Getters y setters
    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public ModalidadPago getModalidadPago() {
        return modalidadPago;
    }

    public void setModalidadPago(ModalidadPago modalidadPago) {
        this.modalidadPago = modalidadPago;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(LocalDate fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    // proyecto - postulaciones
    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference
    private List<Postulacion> postulaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_proyecto", nullable = false)
    private EstadoProyecto estadoProyecto = EstadoProyecto.PUBLICADO;

    public List<Postulacion> getPostulaciones() {
        return postulaciones;
    }

    public void setPostulaciones(List<Postulacion> postulaciones) {
        this.postulaciones = postulaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    // ACTIVACION Y DESACTIVACION DE UN PROYECTO
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
        if ("Abierto".equalsIgnoreCase(estado)) {
            this.estadoProyecto = EstadoProyecto.PUBLICADO;
        } else {
            if (this.estadoProyecto == null || this.estadoProyecto == EstadoProyecto.PUBLICADO) {
                this.estadoProyecto = EstadoProyecto.EN_PROGRESO;
            }
        }
    }

    public String getCreadorCorreo() {
        return creadorCorreo;
    }

    public void setCreadorCorreo(String creadorCorreo) {
        this.creadorCorreo = creadorCorreo;
    }

    // ESTADO OFICIAL DE UN PROYECTO
    public EstadoProyecto getEstadoProyecto() {
        return estadoProyecto;
    }

    public void setEstadoProyecto(EstadoProyecto estadoProyecto) {
        this.estadoProyecto = estadoProyecto;
        if (estadoProyecto == EstadoProyecto.PUBLICADO) {
            this.estado = "Abierto";
        } else {
            this.estado = "Cerrado";
        }
    }

    @AssertTrue(message = "Las fechas deben estar entre hoy y un año a partir de hoy, y la final debe ser posterior a la inicio")
    public boolean isRangoFechasValido() {
        if (fechaInicio == null || fechaFinal == null)
            return true;

        LocalDate hoy = LocalDate.now();
        LocalDate maxFecha = hoy.plusYears(1);

        return !fechaInicio.isBefore(hoy) &&
                !fechaInicio.isAfter(maxFecha) &&
                !fechaFinal.isBefore(hoy) &&
                !fechaFinal.isAfter(maxFecha) &&
                fechaFinal.isAfter(fechaInicio);
    }

}
