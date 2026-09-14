package com.workeando.plataform.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "freelancer")
public class Freelancer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFreelancer;

    @OneToOne
    @JoinColumn(name = "idUsuario", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    @Size(max = 200, message = "El enlace del portafolio es demasiado largo")
    private String portafolio;

    @Size(max = 200, message = "El enlace de LinkedIn es demasiado largo")
    private String linkedin;

    @JsonManagedReference
    @MaxCategorias(value = 3, message = "Solo puedes seleccionar hasta 3 categorías")
    @ManyToMany
    @JoinTable(name = "freelancer_categoria", joinColumns = @JoinColumn(name = "id_freelancer"), inverseJoinColumns = @JoinColumn(name = "id_categoria"))
    private List<Categoria> categorias = new ArrayList<>();

    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?\\d{6,14}$", message = "El teléfono debe tener entre 6 y 14 dígitos")
    private String telefono;

    @Valid
    @ElementCollection
    @CollectionTable(name = "freelancer_experiencia", joinColumns = @JoinColumn(name = "freelancer_id"))
    private List<ExperienciaLaboral> experienciaLaboral = new ArrayList<>();

    @Valid
    @ElementCollection
    @CollectionTable(name = "freelancer_idiomas", joinColumns = @JoinColumn(name = "freelancer_id"))
    private List<Idioma> idiomas = new ArrayList<>();

    @Valid
    @ElementCollection
    @CollectionTable(name = "freelancer_habilidades", joinColumns = @JoinColumn(name = "freelancer_id"))
    private List<HabilidadTecnica> habilidadesTecnicas = new ArrayList<>();

    @Lob
    @Column(name = "cv_archivo")
    private byte[] cvArchivo;

    @Column(name = "nivel_estudios")
    @NotBlank(message = "Debes indicar tu nivel de estudios")
    private String nivelEstudios;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    @Column(nullable = false)
    private boolean esPerfilPorDefecto = false;

    // Getters y Setters

    public Freelancer getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(Freelancer freelancer) {
        this.freelancer = freelancer;
    }

      public boolean getEsPerfilPorDefecto() {
        return esPerfilPorDefecto;
    }

    public void setEsPerfilPorDefecto(boolean esPerfilPorDefecto) {
        this.esPerfilPorDefecto = esPerfilPorDefecto;
    }

    public Long getIdFreelancer() {
        return idFreelancer;
    }

    public void setIdFreelancer(Long idFreelancer) {
        this.idFreelancer = idFreelancer;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getPortafolio() {
        return portafolio;
    }

    public void setPortafolio(String portafolio) {
        this.portafolio = portafolio;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<ExperienciaLaboral> getExperienciaLaboral() {
        return experienciaLaboral;
    }

    public void setExperienciaLaboral(List<ExperienciaLaboral> experienciaLaboral) {
        this.experienciaLaboral = experienciaLaboral;
    }

    public List<Idioma> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<Idioma> idiomas) {
        this.idiomas = idiomas;
    }

    public List<HabilidadTecnica> getHabilidadesTecnicas() {
        return habilidadesTecnicas;
    }

    public void setHabilidadesTecnicas(List<HabilidadTecnica> habilidadesTecnicas) {
        this.habilidadesTecnicas = habilidadesTecnicas;
    }

    public byte[] getCvArchivo() {
        return cvArchivo;
    }

    public void setCvArchivo(byte[] cvArchivo) {
        this.cvArchivo = cvArchivo;
    }

    public String getNivelEstudios() {
        return nivelEstudios;
    }

    public void setNivelEstudios(String nivelEstudios) {
        this.nivelEstudios = nivelEstudios;
    }

    public boolean isPerfilCompleto() {
        return categorias != null && !categorias.isEmpty()
                && habilidadesTecnicas != null && !habilidadesTecnicas.isEmpty()
                && idiomas != null && !idiomas.isEmpty()
                && telefono != null && !telefono.isBlank()
                && nivelEstudios != null && !nivelEstudios.isBlank();
    }
}