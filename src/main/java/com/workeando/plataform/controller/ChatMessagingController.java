package com.workeando.plataform.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.workeando.plataform.dto.ChatMessageDTO;
import com.workeando.plataform.model.Chat;
import com.workeando.plataform.service.ChatService;

@Controller
public class ChatMessagingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    public ChatMessagingController(SimpMessagingTemplate messagingTemplate, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
    }

    // El cliente enviará a /app/chat.send
    @MessageMapping("/chat.send")
    public void enviar(ChatMessageDTO msg) {
        if (msg == null)
            return;
        if (msg.getPostulacionId() == null)
            return;
        if (msg.getRemitenteId() == null || msg.getDestinatarioId() == null)
            return;
        if (msg.getContenido() == null || msg.getContenido().trim().isEmpty())
            return;

        // Guardar mensaje
        Chat saved = chatService.enviarMensaje(
                msg.getPostulacionId(),
                msg.getRemitenteId(),
                msg.getDestinatarioId(),
                msg.getContenido().trim());

        // Preparar DTO de salida
        ChatMessageDTO out = new ChatMessageDTO();
        out.setPostulacionId(saved.getPostulacion().getId());
        out.setRemitenteId(saved.getRemitenteId());
        out.setRemitenteNombre(msg.getRemitenteNombre());
        out.setDestinatarioId(saved.getDestinatarioId());
        out.setContenido(saved.getContenido());
        out.setTimestamp(saved.getFechaEnvio()
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant().toEpochMilli());

        // Enviar mensaje a ambos usuarios conectados
        messagingTemplate.convertAndSend("/queue/chat/" + out.getDestinatarioId(), out);
        messagingTemplate.convertAndSend("/queue/chat/" + out.getRemitenteId(), out);
    }
}
