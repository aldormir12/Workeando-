package com.workeando.plataform.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.workeando.plataform.dto.ChatConversationView;
import com.workeando.plataform.model.Chat;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.repository.PostulacionRepository;
import com.workeando.plataform.service.ChatService;
import com.workeando.plataform.service.UsuarioService;

@Controller
public class ChatController {

    private final UsuarioService usuarioService;
    private final PostulacionRepository postulacionRepository;
    private final ChatService chatService;

    public ChatController(UsuarioService usuarioService,
                          PostulacionRepository postulacionRepository,
                          ChatService chatService) {
        this.usuarioService = usuarioService;
        this.postulacionRepository = postulacionRepository;
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public String bandejaChat(@RequestParam(name = "postulacionId", required = false) Long postulacionId,
                              Model model,
                              Principal principal) {

        // 1) Usuario logueado
        Usuario usuario = usuarioService.buscarPorCorreo(principal.getName());
        model.addAttribute("usuario", usuario);

        // 2) Traer TODAS las postulaciones relacionadas con este usuario
        List<Postulacion> postulaciones;
        if (usuario.getRol() == Usuario.Rol.FREELANCER) {
            // Soy freelancer -> son MIS postulaciones enviadas
            postulaciones = postulacionRepository.findByCorreoFreelancer(usuario.getCorreo());
        } else {
            // Soy empleador -> son las postulaciones que llegaron a MIS proyectos
            postulaciones = postulacionRepository.findByProyectoCreadorCorreo(usuario.getCorreo());
        }

        // 3) Armar lista de conversaciones (una por postulación)
        List<ChatConversationView> conversaciones = new ArrayList<>();

        for (Postulacion p : postulaciones) {
            Usuario otroUsuario;

            if (usuario.getRol() == Usuario.Rol.FREELANCER) {
                // Yo = freelancer -> otro = empleador (creador del proyecto)
                String correoEmpleador = p.getProyecto().getCreadorCorreo();
                otroUsuario = usuarioService.buscarPorCorreo(correoEmpleador);
            } else {
                // Yo = empleador -> otro = freelancer que postuló
                String correoFreelancer = p.getCorreoFreelancer();
                otroUsuario = usuarioService.buscarPorCorreo(correoFreelancer);
            }

            Chat ultimo = chatService.obtenerUltimoMensaje(p.getId());

            ChatConversationView cv = new ChatConversationView();
            cv.setPostulacionId(p.getId());
            cv.setProyectoTitulo(p.getProyecto().getTitulo());
            cv.setOtroUsuarioId(otroUsuario.getId());
            cv.setOtroUsuarioNombre(otroUsuario.getNombre());
            cv.setUltimoMensaje(ultimo != null ? ultimo.getContenido() : "Sin mensajes aún");
            cv.setUltimaFecha(ultimo != null ? ultimo.getFechaEnvio() : null);

            conversaciones.add(cv);
        }

        model.addAttribute("conversaciones", conversaciones);

        // 4) Conversación seleccionada (por defecto, la primera)
        if (postulacionId == null && !conversaciones.isEmpty()) {
            postulacionId = conversaciones.get(0).getPostulacionId();
        }
        model.addAttribute("postulacionSeleccionadaId", postulacionId);

        // 5) Historial de la conversación seleccionada
        List<Chat> mensajes = Collections.emptyList();
        Long destinatarioIdSeleccionado = null;

        if (postulacionId != null) {
            mensajes = chatService.obtenerHistorial(postulacionId);
            chatService.marcarLeidos(postulacionId, usuario.getId());

            for (ChatConversationView cv : conversaciones) {
                if (cv.getPostulacionId().equals(postulacionId)) {
                    destinatarioIdSeleccionado = cv.getOtroUsuarioId();
                    break;
                }
            }
        }

        model.addAttribute("mensajes", mensajes);
        model.addAttribute("destinatarioIdSeleccionado", destinatarioIdSeleccionado);

        return "chat-inbox";
    }
}
