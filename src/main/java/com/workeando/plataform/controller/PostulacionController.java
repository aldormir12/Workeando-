package com.workeando.plataform.controller;

import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.repository.PostulacionRepository;
import com.workeando.plataform.service.FreelancerService;
import com.workeando.plataform.service.PostulacionService;

import com.workeando.plataform.service.ProyectoService;
import com.workeando.plataform.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.Optional;

@Controller
@RequestMapping("/postulaciones")
public class PostulacionController {

    private final ProyectoService proyectoService;
    private final PostulacionRepository postulacionRepository;
    private final UsuarioService usuarioService;
    private final PostulacionService postulacionService;
    private final FreelancerService freelancerService;

    // constructor
    public PostulacionController(ProyectoService proyectoService,
            PostulacionRepository postulacionRepository,
            UsuarioService usuarioService,
            PostulacionService postulacionService,
            FreelancerService freelancerService) {
        this.proyectoService = proyectoService;
        this.postulacionRepository = postulacionRepository;
        this.usuarioService = usuarioService;
        this.postulacionService = postulacionService;
        this.freelancerService = freelancerService;
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

    // Buscar el usuario y obtener su nombre
    Usuario usuario = usuarioService.buscarPorCorreo(correo);
    String nombre = usuario.getNombre();

    // Verificar si ya está postulado
    if (postulacionRepository.existsByProyectoIdAndCorreoFreelancer(proyectoId, correo)) {
        return "redirect:/free?yaPostulado=true";
    }

    // Obtener el freelancer relacionado al usuario
    Freelancer freelancer = freelancerService.buscarPorCorreoUsuario(correo);

    // Crear y guardar la postulación
    Postulacion postulacion = new Postulacion(nombre, correo, montoContraoferta, proyecto);
    postulacion.setFreelancer(freelancer);
    postulacionRepository.save(postulacion);

    return "redirect:/free?postulacionExitosa=true";
}

    @PostMapping("/{id}/aceptar")
    public String aceptarPostulacion(@PathVariable Long id) {
        postulacionService.aceptarPostulacion(id);
        return "redirect:/emple"; // o redirigí a donde estés mostrando las propuestas
    }

    @PostMapping("/{id}/rechazar")
    public String rechazarPostulacion(@PathVariable Long id) {
        postulacionService.rechazarPostulacion(id);
        return "redirect:/emple";
    }

}
