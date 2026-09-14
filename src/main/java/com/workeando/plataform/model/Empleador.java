package com.workeando.plataform.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Empleador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmpleador;

    @OneToOne
    @JoinColumn(name = "Usuario_idUsuario")
    private Usuario usuario;

    private String empresa;

    @OneToMany(mappedBy = "empleador")
    private List<Proyecto> proyectos;

    public Integer getIdEmpleador() {
        return idEmpleador;
    }

    public void setIdEmpleador(Integer idEmpleador) {
        this.idEmpleador = idEmpleador;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public List<Proyecto> getProyectos() {
        return proyectos;
    }

    public void setProyectos(List<Proyecto> proyectos) {
        this.proyectos = proyectos;
    }
}