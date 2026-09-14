package com.workeando.plataform.controller;

import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.repository.PostulacionRepository;
import com.workeando.plataform.service.impl.CategoriaServiceImpl;
import com.workeando.plataform.service.impl.ProyectoServiceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication; //info del usuario autenticado
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class EmpleadorController {

    private final ProyectoServiceImpl proyectoService;
    private final PostulacionRepository postulacionRepository;
    private final CategoriaServiceImpl categoriaService;

    public EmpleadorController(ProyectoServiceImpl proyectoService, PostulacionRepository postulacionRepository, CategoriaServiceImpl categoriaService) {
        this.proyectoService = proyectoService;
        this.postulacionRepository = postulacionRepository;
        this.categoriaService = categoriaService;
    }

    // Vista principal del empleador con sus proyectos paginados
    @GetMapping("/emple")
    public String empleadorPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "fechaPublicacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Model model, 
            Authentication authentication) {
        
        String correo = authentication.getName();
        model.addAttribute("proyecto", new Proyecto());

        // Configurar paginación y ordenamiento
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);

        // proyectos creados por este empleador con paginación
        Page<Proyecto> proyectosPage = proyectoService.listarTodosPorCorreoPaginado(correo, pageable);
        
        model.addAttribute("proyectosPage", proyectosPage);
        model.addAttribute("proyectos", proyectosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", proyectosPage.getTotalPages());
        model.addAttribute("totalElements", proyectosPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);
        
        // Calcular rango de páginas para mostrar en la paginación
        int startPage = Math.max(0, page - 2);
        int endPage = Math.min(proyectosPage.getTotalPages() - 1, page + 2);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        // postulaciones recibidas
        List<Postulacion> postulaciones = postulacionRepository.findByProyectoCreadorCorreo(correo);
        model.addAttribute("postulaciones", postulaciones);

        //agregar categorias al modelo 
        model.addAttribute("categorias", categoriaService.listarTodas());

        return "emple"; // Vista principal del empleador
    }

    // Publicar un nuevo proyecto
    @PostMapping("/empleador/publicar")
    public String publicarProyecto(@ModelAttribute Proyecto proyecto, Authentication authentication) {
        String correo = authentication.getName(); // obtener correo del empleador
        proyecto.setCreadorCorreo(correo); // asignarlo al proyecto

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

        // Actualizar campos
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
        return "postulaciones-empleador"; // Vista a crear
    }
}