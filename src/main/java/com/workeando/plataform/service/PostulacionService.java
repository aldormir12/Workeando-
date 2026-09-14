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



  // paginación
  Page<Postulacion> listarPostulacionesPorCreadorCorreo(String correo, Pageable pageable);
  List<Postulacion> buscarPorFreelancer(Freelancer freelancer);

}
