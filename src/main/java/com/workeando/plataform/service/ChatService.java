package com.workeando.plataform.service;

import com.workeando.plataform.model.Chat;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.repository.ChatRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;

    @PersistenceContext
    private EntityManager em;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    // Guarda un nuevo mensaje en una postulación
    public Chat enviarMensaje(Long postulacionId, Long remitenteId, Long destinatarioId, String contenido) {
        if (postulacionId == null || remitenteId == null || destinatarioId == null) {
            throw new IllegalArgumentException("Ids requeridos");
        }
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("Contenido requerido");
        }

        // Referencia a la postulación; valida existencia
        Postulacion postulacionRef = em.getReference(Postulacion.class, postulacionId);
        if (postulacionRef == null) {
            throw new EntityNotFoundException("Postulación no encontrada");
        }

        Chat chat = new Chat();
        chat.setPostulacion(postulacionRef);
        chat.setRemitenteId(remitenteId);
        chat.setDestinatarioId(destinatarioId);
        chat.setContenido(contenido.trim());
        chat.setFechaEnvio(LocalDateTime.now());
        chat.setLeido(false);

        return chatRepository.save(chat);
    }

    // Historial completo por postulación
    public List<Chat> listarMensajesPorPostulacion(Long postulacionId) {
        return chatRepository.findByPostulacion_IdOrderByFechaEnvioAsc(postulacionId);
    }

    // Último mensaje de la postulación
    public Chat obtenerUltimoMensaje(Long postulacionId) {
        return chatRepository.findTopByPostulacion_IdOrderByFechaEnvioDesc(postulacionId);
    }

    // Postulaciones donde participa el usuario (para bandeja)
    public List<Long> listarPostulacionesDelUsuario(Long usuarioId) {
        return chatRepository.findPostulacionIdsByUsuarioParticipante(usuarioId);
    }

    // Marca como leídos los mensajes recibidos por un usuario en una postulación
    public int marcarLeidos(Long postulacionId, Long usuarioId) {
        List<Chat> mensajes = chatRepository.findByPostulacion_IdOrderByFechaEnvioAsc(postulacionId);
        int count = 0;
        for (Chat m : mensajes) {
            if (!m.isLeido() && m.getDestinatarioId().equals(usuarioId)) {
                m.setLeido(true);
                count++;
            }
        }
        if (count > 0) {
            chatRepository.saveAll(mensajes);
        }
        return count;
    }
}
