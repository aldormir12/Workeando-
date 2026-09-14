package com.workeando.plataform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.workeando.plataform.model.Calificacion;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.repository.CalificacionRepository;
import com.workeando.plataform.repository.PostulacionRepository;
import com.workeando.plataform.service.CategoriaService;
import com.workeando.plataform.service.PostulacionService;
import com.workeando.plataform.service.ProyectoService;

@Controller
public class EmpleadorController {

    private final ProyectoService proyectoService;
    private final PostulacionRepository postulacionRepository;
    private final CategoriaService categoriaService;
    private final PostulacionService postulacionService;

    // NUEVO ↓↓↓
    private final CalificacionRepository calificacionRepository;
    // NUEVO ↑↑↑

    @Autowired
    public EmpleadorController(
            ProyectoService proyectoService,
            PostulacionRepository postulacionRepository,
            CategoriaService categoriaService,
            PostulacionService postuuService,
            CalificacionRepository calificacionRepository // <-- NUEVO
    ) {
        this.proyectoService = proyectoService;
        this.postulacionRepository = postulacionRepository;
        this.categoriaService = categoriaService;
        this.postulacionService = postuuService;
        this.calificacionRepository = calificacionRepository; // <-- NUEVO
    }

    // Vista principal del empleador con sus proyectos
    @GetMapping("/emple")
    public String empleadorPage(Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int pageProyectos,
            @RequestParam(defaultValue = "0") int pagePostulaciones) {

        String correo = authentication.getName();

        model.addAttribute("proyecto", new Proyecto());

        Pageable pageableProyectos = PageRequest.of(pageProyectos, 5);
        Pageable pageablePostulaciones = PageRequest.of(pagePostulaciones, 5);

        Page<Proyecto> proyectosPage = proyectoService.listarPorCorreoPaginado(correo, pageableProyectos);
        Page<Postulacion> postulacionesPage = postulacionService.listarPostulacionesPorCreadorCorreo(correo,
                pageablePostulaciones);

        model.addAttribute("proyectos", proyectosPage.getContent());
        model.addAttribute("proyectosPage", proyectosPage);

        model.addAttribute("postulaciones", postulacionesPage.getContent());
        model.addAttribute("postulacionesPage", postulacionesPage);

        model.addAttribute("categorias", categoriaService.listarTodas());

        // ----------------------------------------------------
        // 🔥 NUEVO: RATING DEL EMPLEADOR
        // ----------------------------------------------------
        List<Calificacion> califsEmpleador =
                calificacionRepository.findByContrato_Postulacion_Proyecto_CreadorCorreo(correo);

        double ratingPromedio = 0.0;
        int ratingTotal = califsEmpleador.size();

        if (ratingTotal > 0) {
            int suma = califsEmpleador.stream()
                    .mapToInt(Calificacion::getPuntuacion)
                    .sum();
            ratingPromedio = (double) suma / ratingTotal;
        }

        model.addAttribute("ratingPromedio", ratingPromedio);
        model.addAttribute("ratingTotal", ratingTotal);
        // ----------------------------------------------------

        return "emple";
    }

    // Publicar un nuevo proyecto
    @PostMapping("/empleador/publicar")
    public String publicarProyecto(@ModelAttribute Proyecto proyecto, Authentication authentication) {
        String correo = authentication.getName();
        proyecto.setCreadorCorreo(correo);

        if (proyecto.getFechaInicio() != null && proyecto.getFechaFinal() != null) {
            if (proyecto.getFechaInicio().isAfter(proyecto.getFechaFinal())) {
                return "redirect:/emple?error=fechasInvalidas";
            }
        }

        proyectoService.guardar(proyecto);
        return "redirect:/emple";
    }

    // Cambiar estado del proyecto entre Abierto y Cerrado
    @PostMapping("/empleador/cambiarEstado/{id}")
    @ResponseBody
    public Map<String, String> cambiarEstado(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if ("Abierto".equals(proyecto.getEstado())) {
            proyecto.setEstado("Cerrado");
        } else {
            proyecto.setEstado("Abierto");
        }

        proyectoService.guardar(proyecto);
        return Map.of("nuevoEstado", proyecto.getEstado());
    }

    // Eliminar proyecto
    @PostMapping("/empleador/eliminarProyecto/{id}")
    @ResponseBody
    public Map<String, Object> eliminarProyecto(@PathVariable Long id) {
        try {
            proyectoService.eliminar(id);
            return Map.of("eliminado", true);
        } catch (Exception e) {
            return Map.of("eliminado", false, "error", e.getMessage());
        }
    }

    // Obtener proyecto para edición - devuelve JSON
    @GetMapping("/empleador/proyecto/{id}")
    @ResponseBody
    public Proyecto obtenerProyecto(@PathVariable Long id) {
        return proyectoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
    }

    // Guardar proyecto editado desde modal
    @PostMapping("/empleador/editarProyecto/{id}")
    public String editarProyecto(@PathVariable Long id, @ModelAttribute Proyecto proyectoEditado) {
        Proyecto proyectoOriginal = proyectoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        proyectoOriginal.setTitulo(proyectoEditado.getTitulo());
        proyectoOriginal.setDescripcion(proyectoEditado.getDescripcion());
        proyectoOriginal.setCategoria(proyectoEditado.getCategoria());
        proyectoOriginal.setPresupuesto(proyectoEditado.getPresupuesto());
        proyectoOriginal.setModalidadPago(proyectoEditado.getModalidadPago());
        proyectoOriginal.setUbicacion(proyectoEditado.getUbicacion());
        proyectoOriginal.setFechaInicio(proyectoEditado.getFechaInicio());
        proyectoOriginal.setFechaFinal(proyectoEditado.getFechaFinal());
        proyectoOriginal.setModalidad(proyectoEditado.getModalidad());

        proyectoService.guardar(proyectoOriginal);

        return "redirect:/emple";
    }

    // Ver todas las postulaciones recibidas
    @GetMapping("/empleador/postulaciones")
    public String verPostulacionesRecibidas(Model model, Authentication authentication) {
        String correoEmpleador = authentication.getName();
        List<Postulacion> postulaciones = postulacionRepository.findByProyectoCreadorCorreo(correoEmpleador);
        model.addAttribute("postulaciones", postulaciones);
        return "postulaciones-empleador";
    }
}
