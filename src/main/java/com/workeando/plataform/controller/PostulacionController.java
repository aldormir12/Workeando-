package com.workeando.plataform.controller;

import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.repository.PostulacionRepository;
import com.workeando.plataform.service.impl.ProyectoServiceImpl;
import com.workeando.plataform.service.impl.UsuarioServiceImpl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.Optional;

@Controller
@RequestMapping("/postulaciones")
public class PostulacionController {

    private final ProyectoServiceImpl proyectoService;
    private final PostulacionRepository postulacionRepository;
    private final UsuarioServiceImpl usuarioService;

    // constructor
    public PostulacionController(ProyectoServiceImpl proyectoService,
            PostulacionRepository postulacionRepository,
            UsuarioServiceImpl usuarioService) {
        this.proyectoService = proyectoService;
        this.postulacionRepository = postulacionRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/existe")
    @ResponseBody
    public Map<String, Boolean> verificarSiYaPostulado(@RequestParam Long proyectoId, Authentication auth) {
        String correo = auth.getName();
        boolean yaPostulado = postulacionRepository.existsByProyectoIdAndCorreoFreelancer(proyectoId, correo);
        return Map.of("yaPostulado", yaPostulado);
    }

    @PostMapping("/{proyectoId}")
    public String postular(@PathVariable Long proyectoId,
            @RequestParam(required = false) Double montoContraoferta,
            Authentication authentication) {

        // Obtener proyecto
        Optional<Proyecto> proyectoOpt = proyectoService.buscarPorId(proyectoId);
        if (proyectoOpt.isEmpty()) {
            return "redirect:/free"; // si no existe pasa a la pagina de free
        }

        Proyecto proyecto = proyectoOpt.get();

        // Obtener datos del usuario autenticado
        String correo = authentication.getName();

        // Buscar el usuario y obtener su nombre y correo
        Usuario usuario = usuarioService.buscarPorCorreo(correo);
        String nombre = usuario.getNombre();

        // Verificar si ya está postulado
        if (postulacionRepository.existsByProyectoIdAndCorreoFreelancer(proyectoId, correo)) {
            return "redirect:/free?yaPostulado=true";
        }

        Postulacion postulacion = new Postulacion(nombre, correo, montoContraoferta, proyecto);
        postulacionRepository.save(postulacion);

        return "redirect:/free?postulacionExitosa=true";

    }

}
