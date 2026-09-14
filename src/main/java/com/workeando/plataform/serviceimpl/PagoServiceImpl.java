package com.workeando.plataform.serviceimpl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workeando.plataform.model.Contrato;
import com.workeando.plataform.model.Pago;
import com.workeando.plataform.model.Pago.EstadoPago;
import com.workeando.plataform.model.Pago.MetodoPago;
import com.workeando.plataform.repository.PagoRepository;
import com.workeando.plataform.service.PagoService;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    public PagoServiceImpl(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    @Transactional
    public Pago crearPagoPendiente(Contrato contrato, Double monto, String moneda, MetodoPago metodo) {

        // Si ya existe un pago para este contrato, lo devolvemos
        Optional<Pago> existente = pagoRepository.findByContrato_IdContrato(contrato.getIdContrato());
        if (existente.isPresent()) {
            return existente.get();
        }

        Pago pago = new Pago();
        pago.setContrato(contrato);
        pago.setMonto(monto);
        pago.setMoneda(moneda != null ? moneda : "PEN");
        pago.setMetodoPago(metodo != null ? metodo : MetodoPago.TRANSFERENCIA);
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setFechaCreacion(LocalDateTime.now());
        pago.setDescripcion("Pago por contrato #" + contrato.getIdContrato());

        return pagoRepository.save(pago);
    }

    @Override
    @Transactional
    public Pago marcarComoAprobado(Pago pago, String referenciaExterna) {
        pago.setEstado(EstadoPago.APROBADO);
        pago.setFechaConfirmacion(LocalDateTime.now());

        // referencia simulada si no envían una
        if (referenciaExterna == null || referenciaExterna.isBlank()) {
            referenciaExterna = "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        pago.setReferenciaExterna(referenciaExterna);

        return pagoRepository.save(pago);
    }

    @Override
    public Optional<Pago> buscarPorContrato(Integer idContrato) {
        return pagoRepository.findByContrato_IdContrato(idContrato);
    }
}
