package com.workeando.plataform.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Postulacion;

public interface PostulacionService {

  List<Postulacion> obtenerTodas();

  Postulacion obtenerPorId(Long id);

  Postulacion guardar(Postulacion postulacion);

  void eliminar(Long id);

  void aceptarPostulacion(Long idPostulacion);

  void rechazarPostulacion(Long idPostulacion);

  // Paginación para el empleador
  Page<Postulacion> listarPostulacionesPorCreadorCorreo(String correo, Pageable pageable);

  // Búsqueda por freelancer
  List<Postulacion> buscarPorFreelancer(Freelancer freelancer);

  void marcarVisto(Long id);

  void marcarFinalista(Long id, boolean valor);
}
