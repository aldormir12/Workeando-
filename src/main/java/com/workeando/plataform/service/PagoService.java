package com.workeando.plataform.service;

import java.util.Optional;

import com.workeando.plataform.model.Contrato;
import com.workeando.plataform.model.Pago;


public interface PagoService {

    Pago crearPagoPendiente(Contrato contrato, Double monto, String moneda, Pago.MetodoPago metodo);

    Pago marcarComoAprobado(Pago pago, String referenciaExterna);

    Optional<Pago> buscarPorContrato(Integer idContrato);
}
