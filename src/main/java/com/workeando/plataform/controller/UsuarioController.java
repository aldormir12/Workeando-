package com.workeando.plataform.controller;

import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.model.Usuario.Rol;
import com.workeando.plataform.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Vista: formulario de registro
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", Rol.values());
        return "registro"; 
    }

    // procesar el formulario
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model) {

        // Si hay errores, permanecer en el formulario con los datos ingresados
        if (result.hasErrors()) {
            model.addAttribute("roles", Usuario.Rol.values());
            return "registro";
        }

        // Asignar rol por defecto si no se seleccionó
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.FREELANCER);
        }

        usuarioService.registrar(usuario);

        // Mostrar modal, limpiar formulario
        model.addAttribute("registroExitoso", true);
        model.addAttribute("usuario", new Usuario()); // se limpia solo si fue exitoso
        model.addAttribute("roles", Usuario.Rol.values());

        return "registro";
    }

    // API: listar todos los usuarios
    @ResponseBody
    @GetMapping("/api")
    public List<Usuario> listarUsuariosAPI() {
        return usuarioService.listarTodos();
    }

    // API: buscar por ID
    @ResponseBody
    @GetMapping("/api/{id}")
    public Optional<Usuario> obtenerUsuario(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    // API: registrar usuario
    @ResponseBody
    @PostMapping("/api")
    public Usuario registrarUsuarioAPI(@RequestBody Usuario usuario) {
        return usuarioService.registrar(usuario);
    }

    // API: eliminar usuario
    @ResponseBody
    @DeleteMapping("/api/{id}")
    public void eliminarUsuarioAPI(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}
