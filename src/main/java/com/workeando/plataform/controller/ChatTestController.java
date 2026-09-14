package com.workeando.plataform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controlador simple para mostrar la vista de prueba del chat
@Controller
public class ChatTestController {

    // Mapea GET /chat-probador a templates/chat-probador.html
    @GetMapping("/chat-probador")
    public String chatProbador() {
        // Debe existir src/main/resources/templates/chat-probador.html
        return "chat-probador";
    }
}
