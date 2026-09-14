package com.workeando.plataform.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    // Nombre único y obligatorio de la categoría
    @Column(nullable = false, unique = true)
    private String nombre;

    // Relación con proyectos (unidireccional desde Proyecto)
    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Proyecto> proyectos;

    // Relación muchos-a-muchos con freelancer (inversa)
    @JsonBackReference
    @ManyToMany(mappedBy = "categorias")
    private List<Freelancer> freelancers;

    // Constructor vacío
    public Categoria() {}

    // Constructor con nombre
    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Proyecto> getProyectos() {
        return proyectos;
    }

    public void setProyectos(List<Proyecto> proyectos) {
        this.proyectos = proyectos;
    }

    public List<Freelancer> getFreelancers() {
        return freelancers;
    }

    public void setFreelancers(List<Freelancer> freelancers) {
        this.freelancers = freelancers;
    }
}
