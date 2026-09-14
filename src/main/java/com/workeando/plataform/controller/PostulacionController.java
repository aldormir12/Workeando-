package com.workeando.plataform.controller;

import com.workeando.plataform.model.EstadoProyecto;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.repository.PostulacionRepository;
import com.workeando.plataform.service.FreelancerService;
import com.workeando.plataform.service.PostulacionService;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;

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

    @ResponseBody
    @PostMapping(value = "/{id}/aceptar", produces = "application/json")
    public ResponseEntity<String> aceptarPostulacion(@PathVariable Long id) {
        try {
            postulacionService.aceptarPostulacion(id);

            Postulacion p = postulacionRepository.findById(id)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Postulación no encontrada"));

            Proyecto proyecto = p.getProyecto();
            if (proyecto != null && proyecto.getEstadoProyecto() == EstadoProyecto.PUBLICADO) {
                proyecto.setEstadoProyecto(EstadoProyecto.EN_PROGRESO);
                proyectoService.guardar(proyecto);
            }

            return ResponseEntity.ok("{\"ok\":true}");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(410).body("{\"error\":\"proyecto_eliminado\"}");
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(404).body("{\"error\":\"postulacion_no_encontrada\"}");
        }
    }

    @ResponseBody
    @PostMapping(value = "/{id}/rechazar", produces = "application/json")
    public ResponseEntity<String> rechazarPostulacion(@PathVariable Long id) {
        try {
            postulacionService.rechazarPostulacion(id);
            return ResponseEntity.ok("{\"ok\":true}");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(410).body("{\"error\":\"proyecto_eliminado\"}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("{\"error\":\"postulacion_no_encontrada\"}");
        }
    }

    @PostMapping("/{id}/visto")
    @ResponseBody
    public ResponseEntity<String> marcarVisto(@PathVariable Long id) {
        try {
            postulacionService.marcarVisto(id);
            return ResponseEntity.ok("{\"ok\":true}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("{\"error\":\"postulacion_no_encontrada\"}");
        }
    }

    @PostMapping("/{id}/finalista")
    @ResponseBody
    public ResponseEntity<String> marcarFinalista(@PathVariable Long id) {
        try {
            postulacionService.marcarFinalista(id, true);
            return ResponseEntity.ok("{\"ok\":true}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("{\"error\":\"postulacion_no_encontrada\"}");
        }
    }

    @PostMapping("/{id}/nofinalista")
    @ResponseBody
    public ResponseEntity<String> quitarFinalista(@PathVariable Long id) {
        try {
            postulacionService.marcarFinalista(id, false);
            return ResponseEntity.ok("{\"ok\":true}");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("{\"error\":\"postulacion_no_encontrada\"}");
        }
    }
}
