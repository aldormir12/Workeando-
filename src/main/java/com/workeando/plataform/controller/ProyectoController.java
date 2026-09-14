package com.workeando.plataform.controller;

import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.service.impl.ProyectoServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/proyectos/empleador")
public class ProyectoController {

    private final ProyectoServiceImpl proyectoService;

    @Autowired
    public ProyectoController(ProyectoServiceImpl proyectoService) {
        this.proyectoService = proyectoService;
    }

    // Vista para mostrar los proyectos activos en la página "free.html"
    @GetMapping
    public String mostrarProyectosEmpleador(Model model) {
        List<Proyecto> proyectos = proyectoService.listarTodos();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Proyecto proyecto : proyectos) {
            if (proyecto.getFechaInicio() != null) {
                proyecto.setFechaInicioFormateada(proyecto.getFechaInicio().format(formatter));
            }
            if (proyecto.getFechaFinal() != null) {
                proyecto.setFechaFinalFormateada(proyecto.getFechaFinal().format(formatter));
            }
        }

        model.addAttribute("proyectos", proyectos);
        return "free"; 
    }

    @PostMapping("/crear")
    public String crearProyecto(@ModelAttribute("proyecto") Proyecto proyecto, Model model) {
        proyecto.setEstado("Abierto");
        proyectoService.guardar(proyecto);
        model.addAttribute("registroExitoso", true);
        return "redirect:/emple";
    }
    //se muestran los proyectos en formato json
    @GetMapping("/api")
    @ResponseBody
    public List<Proyecto> listarProyectos() {
        return proyectoService.listarTodos();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Optional<Proyecto> obtenerProyecto(@PathVariable Long id) {
        return proyectoService.buscarPorId(id);
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public void eliminarProyecto(@PathVariable Long id) {
        proyectoService.eliminar(id);
    }

    @GetMapping("/api/estado/{estado}")
    @ResponseBody
    public List<Proyecto> proyectosPorEstado(@PathVariable String estado) {
        return proyectoService.listarPorEstado(estado);
    }

    @GetMapping("/api/creador/{correo}")
    @ResponseBody
    public List<Proyecto> proyectosPorCreador(@PathVariable String correo) {
        return proyectoService.listarPorCreador(correo);
    }
}
