package com.workeando.plataform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.workeando.plataform.service.impl.ProyectoServiceImpl;

@Controller
public class HomeController {

    private final ProyectoServiceImpl proyectoService;

    public HomeController(ProyectoServiceImpl proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("proyectos", proyectoService.listarTodos());
        return "home";  // Esto busca home.html en templates
    }
}
