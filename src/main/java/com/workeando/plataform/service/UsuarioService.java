package com.workeando.plataform.service;

import java.util.List;
import java.util.Optional;

import com.workeando.plataform.model.Usuario;

public interface UsuarioService {

    List<Usuario> listarTodos();

    Optional<Usuario> buscarPorId(Long id);

    Usuario registrar(Usuario usuario);

    void eliminar(Long id);

    Usuario buscarPorCorreo(String correo);
}
