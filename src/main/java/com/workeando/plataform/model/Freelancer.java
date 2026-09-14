package com.workeando.plataform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    @NotEmpty(message = "Debes seleccionar al menos una categoría")
    @MaxCategorias(value = 3, message = "Solo puedes seleccionar hasta 3 categorías")
    @ManyToMany
    @JoinTable(name = "freelancer_categoria", joinColumns = @JoinColumn(name = "id_freelancer"), inverseJoinColumns = @JoinColumn(name = "id_categoria"))
    private List<Categoria> categorias = new ArrayList<>();

    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?\\d{6,14}$", message = "El teléfono debe tener entre 6 y 14 dígitos")
    private String telefono;

    @ElementCollection
    @CollectionTable(name = "freelancer_experiencia", joinColumns = @JoinColumn(name = "freelancer_id"))
    private List<ExperienciaLaboral> experienciaLaboral = new ArrayList<>();

    @ElementCollection
    private List<String> idiomas = new ArrayList<>();

    @ElementCollection
    private List<String> habilidades = new ArrayList<>();

    @Lob
    @Column(name = "cv_archivo")
    private byte[] cvArchivo;

    @Column(name = "nivel_estudios")
    @NotBlank(message = "Debes indicar tu nivel de estudios")
    private String nivelEstudios;

 

    @Transient
    private boolean perfilCompleto;

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

    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    public List<String> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<String> habilidades) {
        this.habilidades = habilidades;
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

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public void setPerfilCompleto(boolean perfilCompleto) {
        this.perfilCompleto = perfilCompleto;
    }
}
