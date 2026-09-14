package com.workeando.plataform.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workeando.plataform.model.EstadoProyecto;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.repository.PostulacionRepository;
import com.workeando.plataform.service.PostulacionService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PostulacionServiceImpl implements PostulacionService {

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Override
    public List<Postulacion> obtenerTodas() {
        return postulacionRepository.findAll();
    }

    @Override
    public Postulacion obtenerPorId(Long id) {
        Optional<Postulacion> optional = postulacionRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public Postulacion guardar(Postulacion postulacion) {
        return postulacionRepository.save(postulacion);
    }

    @Override
    public void eliminar(Long id) {
        postulacionRepository.deleteById(id);
    }

    @Override
    public Page<Postulacion> listarPostulacionesPorCreadorCorreo(String correo, Pageable pageable) {
        return postulacionRepository.findByProyectoCreadorCorreo(correo, pageable);
    }

    @Override
    public List<Postulacion> buscarPorFreelancer(Freelancer freelancer) {
        return postulacionRepository.findByCorreoFreelancer(freelancer.getUsuario().getCorreo());
    }

    @Override
    @Transactional
    public void aceptarPostulacion(Long idPostulacion) {
        Postulacion postulacion = postulacionRepository.findById(idPostulacion)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Postulación no encontrada"));

        Proyecto proyecto = postulacion.getProyecto();

        if (!"Abierto".equalsIgnoreCase(proyecto.getEstado())) {
            throw new IllegalStateException("El proyecto está cerrado y no puede aceptar postulaciones");
        }

        if (proyecto.getEstadoProyecto() != EstadoProyecto.PUBLICADO) {
            throw new IllegalStateException("El proyecto no está en estado PUBLICADO");
        }

        if (!Boolean.TRUE.equals(postulacion.getFinalista())) {
            postulacion.setFinalista(true);
        }

        if (!"Aceptada".equalsIgnoreCase(String.valueOf(postulacion.getEstado()))) {
            postulacion.setEstado("Aceptada");
        }

        if (proyecto.getEstadoProyecto() == EstadoProyecto.PUBLICADO) {
            proyecto.setEstadoProyecto(EstadoProyecto.EN_PROGRESO);
        }

        // Guardar cambios
        postulacionRepository.save(postulacion);

    }

    @Override
    @Transactional
    public void rechazarPostulacion(Long idPostulacion) {
        Postulacion postulacion = postulacionRepository.findById(idPostulacion)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));

        if (!postulacion.getEstado().equals("Pendiente")) {
            throw new IllegalStateException("Solo se pueden rechazar postulaciones pendientes");
        }

        postulacion.setEstado("Rechazada");

        postulacionRepository.save(postulacion);
    }

    @Transactional
    @Override
    public void marcarVisto(Long id) {
        Postulacion p = postulacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Postulación no encontrada"));
        if (Boolean.TRUE.equals(p.getCvVisto()))
            return; // idempotente
        p.setCvVisto(true);
        p.setCvVistoAt(LocalDateTime.now());
        postulacionRepository.save(p);
    }

    @Transactional
    @Override
    public void marcarFinalista(Long id, boolean valor) {
        Postulacion p = postulacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Postulación no encontrada"));
        p.setFinalista(valor);
        postulacionRepository.save(p);
    }
}
