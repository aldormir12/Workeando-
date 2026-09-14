package com.workeando.plataform.repository;

import com.workeando.plataform.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Historial por postulación (ascendente)
    List<Chat> findByPostulacion_IdOrderByFechaEnvioAsc(Long postulacionId);

    // Último mensaje de la postulación
    Chat findTopByPostulacion_IdOrderByFechaEnvioDesc(Long postulacionId);

    // Mensajes donde participa un usuario (remitente o destinatario)
    List<Chat> findByRemitenteIdOrDestinatarioIdOrderByFechaEnvioDesc(Long remitenteId, Long destinatarioId);

    // Ids de postulaciones donde participa un usuario (para la bandeja)
    @Query("""
            select distinct c.postulacion.id
            from Chat c
            where c.remitenteId = :usuarioId or c.destinatarioId = :usuarioId
            """)
    List<Long> findPostulacionIdsByUsuarioParticipante(Long usuarioId);
}
