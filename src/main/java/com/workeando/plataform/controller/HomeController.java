package com.workeando.plataform.controller;

import com.workeando.plataform.service.ProyectoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProyectoService proyectoService;

    public HomeController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("proyectos", proyectoService.listarTodos());
        return "home";
    }
}
