package com.workeando.plataform.repository;

import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {


    //Busca un freelancer por su usuario para saber si el perfil ya fue creado.

    Optional<Freelancer> findByUsuario(Usuario usuario);

    Optional<Freelancer> findByUsuarioCorreo(String correo);

}
