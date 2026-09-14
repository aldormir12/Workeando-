package com.workeando.plataform.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.workeando.plataform.dto.ChatContratoRequest;
import com.workeando.plataform.model.Calificacion;
import com.workeando.plataform.model.Contrato;
import com.workeando.plataform.model.Pago;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.repository.CalificacionRepository;
import com.workeando.plataform.repository.PagoRepository;
import com.workeando.plataform.repository.PostulacionRepository;
import com.workeando.plataform.service.ContratoService;
import com.workeando.plataform.service.UsuarioService;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;

    // DTO para recibir calificación
    public static class CalificacionRequest {

        private Integer puntuacion;
        private String comentario;

        public Integer getPuntuacion() {
            return puntuacion;
        }

        public void setPuntuacion(Integer puntuacion) {
            this.puntuacion = puntuacion;
        }

        public String getComentario() {
            return comentario;
        }

        public void setComentario(String comentario) {
            this.comentario = comentario;
        }
    }

    @GetMapping
    public List<Contrato> listarContratos() {
        return contratoService.listarTodos();
    }

    @PostMapping
    public Contrato guardar(@RequestBody Contrato contrato) {
        return contratoService.guardar(contrato);
    }

    @GetMapping("/{id}")
    public Contrato obtener(@PathVariable Integer id) {
        return contratoService.buscarPorId(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        contratoService.eliminar(id);
    }

    // OBTENER CONTRATO POR POSTULACIÓN
    @GetMapping("/postulacion/{postulacionId}")
    public ResponseEntity<Contrato> obtenerPorPostulacion(@PathVariable Long postulacionId) {
        return contratoService.buscarPorPostulacionId(postulacionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ACTUALIZAR / GENERAR CONTRATO
    @PostMapping("/postulacion/{postulacionId}/generar")
    public ResponseEntity<Contrato> generarContrato(@PathVariable Long postulacionId,
            @RequestBody ChatContratoRequest request,
            Principal principal) {

        Usuario usuarioLogueado = usuarioService.buscarPorCorreo(principal.getName());

        Postulacion postulacion = postulacionRepository.findById(postulacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulación no encontrada"));

        String correoEmpleador = postulacion.getProyecto().getCreadorCorreo();
        if (!correoEmpleador.equals(usuarioLogueado.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No eres el empleador de este proyecto");
        }

        Contrato contrato = contratoService.buscarPorPostulacionId(postulacionId)
                .orElseGet(Contrato::new);

        if (contrato.getIdContrato() != null
                && (contrato.isFirmadoEmpleador() || contrato.isFirmadoFreelancer())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El contrato ya fue firmado y no puede editarse");
        }

        contrato.setPostulacion(postulacion);
        contrato.setTituloProyecto(postulacion.getProyecto().getTitulo());
        contrato.setNombreEmpleador(usuarioLogueado.getNombre());

        String correoFreelancer = postulacion.getCorreoFreelancer();
        Usuario freelancer = usuarioService.buscarPorCorreo(correoFreelancer);
        contrato.setNombreFreelancer(freelancer.getNombre());

        contrato.setDetallesAdicionales(request.getDetallesAdicionales());

        String cuerpo = construirTextoContrato(
                postulacion,
                usuarioLogueado.getNombre(),
                freelancer.getNombre(),
                request.getDetallesAdicionales());
        contrato.setCuerpoContrato(cuerpo);

        // Fecha de creación
        if (contrato.getFechaCreacion() == null) {
            contrato.setFechaCreacion(LocalDate.now());
        }

        Contrato guardado = contratoService.guardar(contrato);
        return ResponseEntity.ok(guardado);
    }

    // MARCAR "ENVIADO"
    @PostMapping("/{id}/enviar")
    public ResponseEntity<Contrato> enviarContrato(@PathVariable Integer id, Principal principal) {
        Usuario usuarioLogueado = usuarioService.buscarPorCorreo(principal.getName());

        Contrato contrato = contratoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        Postulacion postulacion = contrato.getPostulacion();
        String correoEmpleador = postulacion.getProyecto().getCreadorCorreo();

        if (!correoEmpleador.equals(usuarioLogueado.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No eres el empleador de este proyecto");
        }

        contrato.setEnviado(true);
        contrato.setFechaEnvio(LocalDate.now());

        Contrato guardado = contratoService.guardar(contrato);
        return ResponseEntity.ok(guardado);
    }

    // FIRMAR CONTRATO
    @PostMapping("/{id}/firmar")
    public ResponseEntity<Contrato> firmarContrato(@PathVariable Integer id, Principal principal) {
        Usuario usuarioLogueado = usuarioService.buscarPorCorreo(principal.getName());

        Contrato contrato = contratoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        Postulacion postulacion = contrato.getPostulacion();

        String correoEmpleador = postulacion.getProyecto().getCreadorCorreo();
        String correoFreelancer = postulacion.getCorreoFreelancer();

        boolean esEmpleador = correoEmpleador.equals(usuarioLogueado.getCorreo());
        boolean esFreelancer = correoFreelancer.equals(usuarioLogueado.getCorreo());

        if (!esEmpleador && !esFreelancer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No perteneces a esta postulación");
        }

        if (esEmpleador && !contrato.isFirmadoEmpleador()) {
            contrato.setFirmadoEmpleador(true);
            contrato.setFechaFirmaEmpleador(LocalDate.now());
            contrato.setFirmaEmpleadorDigital(construirFirmaDigital(usuarioLogueado));
        } else if (esFreelancer && !contrato.isFirmadoFreelancer()) {
            contrato.setFirmadoFreelancer(true);
            contrato.setFechaFirmaFreelancer(LocalDate.now());
            contrato.setFirmaFreelancerDigital(construirFirmaDigital(usuarioLogueado));
        }

        Contrato guardado = contratoService.guardar(contrato);
        return ResponseEntity.ok(guardado);
    }

    // 🔹 SIMULAR PAGO (liberación de pago, solo empleador y con ambas firmas)
    @PostMapping("/{id}/simular-pago")
    public ResponseEntity<Contrato> simularPago(@PathVariable Integer id, Principal principal) {

        Usuario usuarioLogueado = usuarioService.buscarPorCorreo(principal.getName());

        Contrato contrato = contratoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        Postulacion postulacion = contrato.getPostulacion();
        if (postulacion == null || postulacion.getProyecto() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El contrato no está asociado a un proyecto");
        }

        // Solo el empleador dueño del proyecto puede liberar el pago
        String correoEmpleador = postulacion.getProyecto().getCreadorCorreo();
        if (!correoEmpleador.equals(usuarioLogueado.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No eres el empleador de este proyecto");
        }

        // Ambas partes deben haber firmado
        if (!contrato.isFirmadoEmpleador() || !contrato.isFirmadoFreelancer()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El pago solo se puede liberar cuando ambas partes han firmado el contrato");
        }

        // Si ya existe pago o ya fue marcado como liberado, no hacemos nada
        if (contrato.getPago() != null || contrato.isPagoLiberado()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El pago ya fue liberado para este contrato");
        }

        Double monto = postulacion.getMontoPropuesto();

        if (monto == null || monto <= 0) {
            double presupuesto = postulacion.getProyecto().getPresupuesto();

            if (presupuesto > 0) {
                monto = presupuesto;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No se encontró un monto válido para procesar el pago");
            }
        }

        Pago pago = new Pago();
        pago.setContrato(contrato);
        pago.setMonto(monto);

        Pago pagoGuardado = pagoRepository.save(pago);

        contrato.setPago(pagoGuardado);
        contrato.setPagoLiberado(true);
        contrato.setFechaPagoLiberado(LocalDateTime.now());

        Contrato contratoGuardado = contratoService.guardar(contrato);

        return ResponseEntity.ok(contratoGuardado);
    }

    // 🔹 REGISTRAR CALIFICACIÓN (empleador o freelancer, pago ya liberado)
    @PostMapping("/{id}/calificaciones")
    public ResponseEntity<Contrato> registrarCalificacion(
            @PathVariable Integer id,
            @RequestBody CalificacionRequest request,
            Principal principal) {

        Usuario usuarioLogueado = usuarioService.buscarPorCorreo(principal.getName());

        if (request.getPuntuacion() == null || request.getPuntuacion() < 1 || request.getPuntuacion() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La puntuación debe estar entre 1 y 5");
        }

        Contrato contrato = contratoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        Postulacion postulacion = contrato.getPostulacion();
        if (postulacion == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El contrato no está asociado a una postulación");
        }

        // Solo se puede calificar una vez que el pago haya sido liberado
        if (contrato.getPago() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo puedes calificar una vez que el pago haya sido liberado");
        }

        String correoEmpleador = postulacion.getProyecto().getCreadorCorreo();
        String correoFreelancer = postulacion.getCorreoFreelancer();

        boolean esEmpleador = correoEmpleador != null && correoEmpleador.equals(usuarioLogueado.getCorreo());
        boolean esFreelancer = correoFreelancer != null && correoFreelancer.equals(usuarioLogueado.getCorreo());

        if (!esEmpleador && !esFreelancer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo las partes del contrato pueden calificar");
        }

        Integer calificadorId = usuarioLogueado.getId().intValue();

        // Evitar calificación duplicada por el mismo usuario
        boolean yaCalifico = calificacionRepository
                .existsByContrato_IdContratoAndCalificadorId(id, calificadorId);

        if (yaCalifico) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ya has registrado una calificación para este contrato");
        }

        Calificacion calificacion = new Calificacion();
        calificacion.setContrato(contrato);
        calificacion.setCalificadorId(calificadorId);
        calificacion.setPuntuacion(request.getPuntuacion());
        calificacion.setComentario(request.getComentario());

        calificacionRepository.save(calificacion);

        // Recargar contrato con calificaciones actualizadas
        Contrato contratoActualizado = contratoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        return ResponseEntity.ok(contratoActualizado);
    }

    // CONSTRUIR TEXTO DEL CONTRATO
    private String construirTextoContrato(Postulacion postulacion,
            String nombreEmpleador,
            String nombreFreelancer,
            String detallesAdicionales) {

        String titulo = postulacion.getProyecto().getTitulo();
        String descripcion = postulacion.getProyecto().getDescripcion();

        StringBuilder sb = new StringBuilder();

        sb.append("CONTRATO DE PRESTACIÓN DE SERVICIOS FREELANCE\n\n");

        sb.append("Entre las partes:\n");
        sb.append("EMPLEADOR: ").append(nombreEmpleador).append(".\n");
        sb.append("FREELANCER: ").append(nombreFreelancer).append(".\n\n");

        sb.append("En adelante denominados conjuntamente \"LAS PARTES\", acuerdan lo siguiente:\n\n");

        sb.append("1. OBJETO DEL CONTRATO\n");
        sb.append("El presente contrato tiene por objeto la prestación de servicios freelance para el proyecto denominado \"")
                .append(titulo).append("\".\n\n");

        sb.append("2. ALCANCE DE LOS SERVICIOS\n");
        sb.append("El Freelancer se compromete a realizar las siguientes tareas y/o servicios:\n");
        sb.append(descripcion != null ? descripcion : "Descripción no especificada.").append("\n\n");

        sb.append("3. DETALLES ADICIONALES ACORDADOS\n");
        if (detallesAdicionales != null && !detallesAdicionales.isBlank()) {
            sb.append(detallesAdicionales).append("\n\n");
        } else {
            sb.append("N/A\n\n");
        }

        sb.append("4. DURACIÓN\n");
        sb.append(
                "La duración del presente contrato será la necesaria para la ejecución del proyecto, salvo acuerdo distinto entre las partes.\n\n");

        sb.append("5. HONORARIOS Y FORMA DE PAGO\n");
        sb.append(
                "Los honorarios, forma y plazos de pago serán acordados entre las partes a través de la plataforma Workeando y se entenderán como parte integrante de este contrato.\n\n");

        sb.append("6. PROPIEDAD INTELECTUAL\n");
        sb.append(
                "Todos los entregables y resultados del trabajo podrán ser cedidos al Empleador según los términos acordados, respetando la normativa vigente de propiedad intelectual.\n\n");

        sb.append("7. CONFIDENCIALIDAD\n");
        sb.append(
                "El Freelancer se compromete a mantener estricta confidencialidad sobre toda información proporcionada por el Empleador y a no divulgarla a terceros sin autorización previa y por escrito.\n\n");

        sb.append("8. TERMINACIÓN\n");
        sb.append(
                "Cualquiera de las partes podrá dar por terminado este contrato en caso de incumplimiento grave de la otra parte, previo aviso y conforme a las políticas de la plataforma Workeando.\n\n");

        sb.append("9. LEY APLICABLE Y JURISDICCIÓN\n");
        sb.append(
                "El presente contrato se rige por la legislación aplicable en la jurisdicción del Empleador, salvo pacto en contrario.\n\n");

        sb.append("10. ACEPTACIÓN\n");
        sb.append("Las partes declaran haber leído, entendido y aceptado todas las cláusulas del presente contrato.\n\n");

        sb.append("Lugar y fecha: ").append(LocalDate.now()).append("\n");

        sb.append("\n---\n\n");
        sb.append("EMPLEADOR: ").append(nombreEmpleador).append("\n");
        sb.append("FREELANCER: ").append(nombreFreelancer).append("\n");

        return sb.toString();
    }

    // FIRMA DIGITAL
    private String construirFirmaDigital(Usuario usuario) {
        return "Firmado digitalmente por " + usuario.getNombre()
                + " (" + usuario.getCorreo() + ") en fecha " + LocalDate.now();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Integer id, Principal principal) {
        Contrato contrato = contratoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        Usuario usuarioLogueado = usuarioService.buscarPorCorreo(principal.getName());
        Postulacion postulacion = contrato.getPostulacion();

        String correoEmpleador = postulacion.getProyecto().getCreadorCorreo();
        String correoFreelancer = postulacion.getCorreoFreelancer();

        boolean esEmpleador = correoEmpleador.equals(usuarioLogueado.getCorreo());
        boolean esFreelancer = correoFreelancer.equals(usuarioLogueado.getCorreo());

        if (!esEmpleador && !esFreelancer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No perteneces a esta postulación");
        }

        try {
            Context context = new Context();
            context.setVariable("contrato", contrato);
            String html = templateEngine.process("contrato-pdf", context);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);
            renderer.finishPDF();

            byte[] pdfBytes = baos.toByteArray();
            String fileName = "contrato-" + contrato.getIdContrato() + ".pdf";

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(pdfBytes);
        } catch (DocumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al generar el PDF del contrato", e);
        }

    }

    @PostMapping("/{id}/firma-imagen")
    public ResponseEntity<Contrato> subirFirmaImagen(@PathVariable Integer id,
            @RequestParam("archivo") MultipartFile archivo,
            Principal principal) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se envió archivo");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo debe ser una imagen");
        }

        Usuario usuarioLogueado = usuarioService.buscarPorCorreo(principal.getName());

        Contrato contrato = contratoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        Postulacion postulacion = contrato.getPostulacion();

        String correoEmpleador = postulacion.getProyecto().getCreadorCorreo();
        String correoFreelancer = postulacion.getCorreoFreelancer();

        boolean esEmpleador = correoEmpleador.equals(usuarioLogueado.getCorreo());
        boolean esFreelancer = correoFreelancer.equals(usuarioLogueado.getCorreo());

        if (!esEmpleador && !esFreelancer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No perteneces a esta postulación");
        }

        try {
            // carpeta local para guardar firmas
            Path uploadDir = Paths.get("firmas");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String extension = ".png";
            if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) {
                extension = ".jpg";
            }

            String baseName = "contrato-" + contrato.getIdContrato() + (esEmpleador ? "-empleador" : "-freelancer");
            Path destino = uploadDir.resolve(baseName + extension);

            Files.write(destino, archivo.getBytes());

            // URL de archivo para FlyingSaucer (file:///...)
            String fileUrl = destino.toUri().toString();

            if (esEmpleador) {
                contrato.setFirmaEmpleadorImagenPath(fileUrl);
            } else {
                contrato.setFirmaFreelancerImagenPath(fileUrl);
            }

            Contrato guardado = contratoService.guardar(contrato);
            return ResponseEntity.ok(guardado);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar la imagen de la firma", e);
        }
    }

}
