package com.workeando.plataform.service;

import java.util.List;
import java.util.Optional;

import com.workeando.plataform.model.ExperienciaLaboral;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Usuario;

public interface FreelancerService {

    List<String> extraerHabilidades(String texto);

    List<String> extraerIdiomas(String texto);

    List<ExperienciaLaboral> extraerExperienciasLaborales(String texto);

    boolean perfilExiste(Usuario usuario);

    Optional<Freelancer> buscarPorUsuario(Usuario usuario);

    void guardarFreelancer(Freelancer freelancer);
}
