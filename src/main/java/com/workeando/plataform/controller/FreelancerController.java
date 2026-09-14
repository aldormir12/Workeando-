package com.workeando.plataform.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map; // <-- NUEVO
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // <-- NUEVO
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.workeando.plataform.model.Calificacion;
import com.workeando.plataform.model.ExperienciaLaboral;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.repository.CalificacionRepository;
import com.workeando.plataform.service.CategoriaService;
import com.workeando.plataform.service.FreelancerService;
import com.workeando.plataform.service.FypService;
import com.workeando.plataform.service.PostulacionService;
import com.workeando.plataform.service.ProyectoService;
import com.workeando.plataform.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class FreelancerController {

    private final ProyectoService proyectoService;
    private final FreelancerService freelancerService;
    private final UsuarioService usuarioService;
    private final FypService fypService;
    private final CategoriaService categoriaService;
    private final PostulacionService postulacionService;

    // ⬇️⬇️ NUEVO: inyectar CalificacionRepository
    private final CalificacionRepository calificacionRepository;

    public FreelancerController(ProyectoService proyectoService,
            FreelancerService freelancerService,
            UsuarioService usuarioService,
            CategoriaService categoriaService,
            PostulacionService postulacionService,
            FypService fypService,
            CalificacionRepository calificacionRepository) { // <-- NUEVO
        this.proyectoService = proyectoService;
        this.freelancerService = freelancerService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
        this.postulacionService = postulacionService;
        this.fypService = fypService;
        this.calificacionRepository = calificacionRepository; // <-- NUEVO
    }

    @GetMapping("/free")
    public String freelancerPage(@RequestParam(required = false) String categoria,
            Model model,
            Authentication authentication) {

        // Obtiene el correo del usuario autenticado
        String email = authentication.getName();

        // Busca al usuario con el correo
        Usuario usuario = usuarioService.buscarPorCorreo(email);
        model.addAttribute("nombre", usuario.getNombre());

        // Verifica si el freelancer ya tiene perfil
        boolean tienePerfil = freelancerService.perfilExiste(usuario);
        model.addAttribute("tienePerfil", tienePerfil);

        if (!tienePerfil) {
            Freelancer perfilPorDefecto = new Freelancer();
            perfilPorDefecto.setUsuario(usuario);
            perfilPorDefecto.setTelefono("");
            perfilPorDefecto.setNivelEstudios("No especificado");
            perfilPorDefecto.setLinkedin("");
            perfilPorDefecto.setPortafolio("");
            perfilPorDefecto.setEsPerfilPorDefecto(true);

            freelancerService.guardarFreelancer(perfilPorDefecto);

            model.addAttribute("freelancer", perfilPorDefecto);
            model.addAttribute("alertaPerfil",
                    "Tu perfil ha sido creado automáticamente con información básica. ¡Completa tu perfil para mejorar tus posibilidades de ser elegido!");
            model.addAttribute("esPerfilPorDefecto", true);

        } else {
            Freelancer freelancer = freelancerService.buscarPorUsuario(usuario).get();

            model.addAttribute("freelancer", freelancer);

            if (freelancer.getEsPerfilPorDefecto()) {
                model.addAttribute("alertaPerfil",
                        "Tu perfil ha sido creado automáticamente con información básica. ¡Completa tu perfil para mejorar tus posibilidades de ser elegido!");
                model.addAttribute("esPerfilPorDefecto", true);
            } else {
                model.addAttribute("esPerfilPorDefecto", false);
            }
        }

        // 🔥🔥 NUEVO: Cargar calificaciones del FREELANCER
        List<Calificacion> califsFreelancer =
                calificacionRepository.findByContrato_Postulacion_Freelancer_Usuario_Id(usuario.getId());

        double ratingPromedio = 0.0;
        int ratingTotal = califsFreelancer.size();

        if (ratingTotal > 0) {
            int suma = califsFreelancer.stream()
                    .mapToInt(Calificacion::getPuntuacion)
                    .sum();
            ratingPromedio = (double) suma / ratingTotal;
        }

        // Enviar al modelo
        model.addAttribute("ratingPromedio", ratingPromedio);
        model.addAttribute("ratingTotal", ratingTotal);
        // 🔥🔥 FIN NUEVO


        // Decide si usar FYP
        boolean usarFyp = false;
        if (tienePerfil) {
            Optional<Freelancer> fOpt = freelancerService.buscarPorUsuario(usuario);
            if (fOpt.isPresent() && !fOpt.get().getEsPerfilPorDefecto()) {
                usarFyp = true;
            }
        }

        List<Proyecto> proyectos;
        if (usarFyp) {
            proyectos = fypService.feedParaUsuario(email, 50);
            model.addAttribute("fyp", true);
        } else {
            if (categoria != null && !categoria.isBlank()) {
                proyectos = proyectoService.listarPorCategoriaYEstado(categoria, "Abierto");
            } else {
                proyectos = proyectoService.listarPorEstado("Abierto");
            }
        }

        model.addAttribute("proyectos", proyectos);
        model.addAttribute("categoriaSeleccionada", categoria);

        return "free";
    }

    // -----------------------------
    // (RESTO DE TU CONTROLADOR SIN CAMBIOS)
    // -----------------------------

    @GetMapping("/free/perfil")
    public String mostrarFormularioPerfil(
            @RequestParam(name = "modoEdicion", required = false, defaultValue = "false") boolean modoEdicion,
            Model model,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorCorreo(email);

        Freelancer freelancer = freelancerService.buscarPorUsuario(usuario)
                .orElse(new Freelancer());

        if (freelancer.getUsuario() == null) {
            freelancer.setUsuario(usuario);
        }

        if (freelancer.getIdiomas().isEmpty()) {
            freelancer.getIdiomas().add(new com.workeando.plataform.model.Idioma());
        }
        if (freelancer.getHabilidadesTecnicas().isEmpty()) {
            freelancer.getHabilidadesTecnicas().add(new com.workeando.plataform.model.HabilidadTecnica());
        }
        if (freelancer.getExperienciaLaboral().isEmpty()) {
            freelancer.getExperienciaLaboral().add(new com.workeando.plataform.model.ExperienciaLaboral());
        }

        model.addAttribute("soloLectura", false);
        model.addAttribute("freelancer", freelancer);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("habilidadesDisponibles", obtenerHabilidadesDisponibles());
        model.addAttribute("cvProcesado", false);
        model.addAttribute("modoEdicion", modoEdicion);
        return "crearPerfil";
    }

    @PostMapping("/free/perfil")
    public String guardarPerfil(
            @ModelAttribute("freelancer") @Valid Freelancer freelancer,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println(" MÉTODO guardarPerfil INVOCADO");

        if (freelancer.getCategorias() == null || freelancer.getCategorias().isEmpty()) {
            result.rejectValue("categorias", "error.categorias", "Debes seleccionar al menos una categoría");
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("cvProcesado", true);
            return "crearPerfil";
        }

        if (freelancer.getPortafolio() != null && !freelancer.getPortafolio().isBlank()) {
            String urlRegex = "^(https?://)?[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$";
            if (!freelancer.getPortafolio().matches(urlRegex)) {
                result.rejectValue("portafolio", "urlInvalida", "El portafolio debe ser una URL válida");
                model.addAttribute("categorias", categoriaService.listarTodas());
                return "crearPerfil";
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        Date fechaActual = new Date();
        String fechaRegex = "^\\d{2}/\\d{4}$";

        for (int i = 0; i < freelancer.getExperienciaLaboral().size(); i++) {
            ExperienciaLaboral exp = freelancer.getExperienciaLaboral().get(i);

            if (exp.getFechaDesde() != null && !exp.getFechaDesde().matches(fechaRegex)) {
                result.rejectValue("experienciaLaboral[" + i + "].fechaDesde", "fechaInvalida",
                        "La fecha de inicio debe tener el formato MM/YYYY");
                model.addAttribute("categorias", categoriaService.listarTodas());
                return "crearPerfil";
            }

            if (exp.getFechaHasta() != null && !exp.getFechaHasta().equalsIgnoreCase("Actualidad")) {
                if (!exp.getFechaHasta().matches(fechaRegex)) {
                    result.rejectValue("experienciaLaboral[" + i + "].fechaHasta", "fechaInvalida",
                            "La fecha de fin debe tener el formato MM/YYYY o ser 'Actualidad'");
                    model.addAttribute("categorias", categoriaService.listarTodas());
                    return "crearPerfil";
                }

                try {
                    Date fechaDesde = sdf.parse(exp.getFechaDesde());
                    Date fechaHasta = sdf.parse(exp.getFechaHasta());

                    if (fechaHasta.before(fechaDesde)) {
                        result.rejectValue("experienciaLaboral[" + i + "].fechaHasta", "fechaInvalida",
                                "La fecha de fin no puede ser anterior a la fecha de inicio");
                        model.addAttribute("categorias", categoriaService.listarTodas());
                        return "crearPerfil";
                    }

                    if (fechaHasta.after(fechaActual)) {
                        result.rejectValue("experienciaLaboral[" + i + "].fechaHasta", "fechaInvalida",
                                "La fecha de fin no puede ser posterior a la fecha actual");
                        model.addAttribute("categorias", categoriaService.listarTodas());
                        return "crearPerfil";
                    }

                } catch (ParseException e) {
                    result.rejectValue("experienciaLaboral[" + i + "].fechaHasta", "fechaInvalida",
                            "Error al procesar las fechas");
                    model.addAttribute("categorias", categoriaService.listarTodas());
                    return "crearPerfil";
                }
            }
        }

        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorCorreo(email);

        Optional<Freelancer> existenteOpt = freelancerService.buscarPorUsuario(usuario);
        Freelancer freelancerPersistente;

        if (existenteOpt.isPresent()) {
            freelancerPersistente = existenteOpt.get();

            freelancerPersistente.setTelefono(freelancer.getTelefono());
            freelancerPersistente.setNivelEstudios(freelancer.getNivelEstudios());
            freelancerPersistente.setLinkedin(freelancer.getLinkedin());
            freelancerPersistente.setPortafolio(freelancer.getPortafolio());
            freelancerPersistente.setCategorias(freelancer.getCategorias());
            freelancerPersistente.setIdiomas(freelancer.getIdiomas());
            freelancerPersistente.getHabilidadesTecnicas().clear();
            freelancerPersistente.getHabilidadesTecnicas().addAll(freelancer.getHabilidadesTecnicas());
            freelancerPersistente.setExperienciaLaboral(freelancer.getExperienciaLaboral());
            freelancerPersistente.setEsPerfilPorDefecto(false);

        } else {
            freelancerPersistente = freelancer;
            freelancerPersistente.setUsuario(usuario);
            freelancerPersistente.setEsPerfilPorDefecto(false);
        }

        freelancerService.guardarFreelancer(freelancerPersistente);

        redirectAttributes.addFlashAttribute("toastExito", "Tu perfil ha sido guardado con éxito.");
        return "redirect:/free";
    }

    @PostMapping("/free/subirCv")
    public String procesarCv(@RequestParam("cvFile") MultipartFile archivo,
            Authentication authentication,
            Model model) {

        if (archivo.isEmpty()) {
            model.addAttribute("toastError", "Por favor, selecciona un archivo.");
            return "redirect:/free";
        }

        try {
            String textoExtraido = freelancerService.extraerTextoDesdeDocx(archivo.getInputStream());

            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorCorreo(email);

            Freelancer freelancer = new Freelancer();
            freelancer.setUsuario(usuario);
            freelancer.setTelefono(freelancerService.extraerTelefono(textoExtraido));
            freelancer.setCvArchivo(archivo.getBytes());
            freelancer.setExperienciaLaboral(freelancerService.extraerExperienciasLaborales(textoExtraido));
            freelancer.setIdiomas(freelancerService.extraerIdiomasComoObjetos(textoExtraido));
            freelancer.setHabilidadesTecnicas(freelancerService.extraerHabilidadesComoObjetos(textoExtraido));

            Map<String, String> enlaces = freelancerService.extraerEnlacesCV(textoExtraido);
            freelancer.setLinkedin(enlaces.getOrDefault("linkedin", ""));
            freelancer.setPortafolio(enlaces.getOrDefault("portafolio", ""));

            freelancer.setNivelEstudios("No especificado");

            model.addAttribute("freelancer", freelancer);
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("habilidadesDisponibles", obtenerHabilidadesDisponibles());
            model.addAttribute("toastExito", "Datos del CV cargados. Por favor, completa o ajusta la información.");
            model.addAttribute("cvProcesado", true);

            return "crearPerfil";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("toastError", "Error al procesar el archivo.");
            return "redirect:/free";
        }
    }

    @GetMapping("/free/mis-postulaciones")
    public String verPostulacionesDelFreelancer(Model model, Authentication authentication) {
        String correo = authentication.getName();
        Usuario usuario = usuarioService.buscarPorCorreo(correo);

        Optional<Freelancer> optFreelancer = freelancerService.buscarPorUsuario(usuario);
        if (optFreelancer.isEmpty()) {
            return "redirect:/free";
        }

        Freelancer freelancer = optFreelancer.get();
        List<Postulacion> postulaciones = postulacionService.buscarPorFreelancer(freelancer);

        model.addAttribute("postulaciones", postulaciones);
        return "misPostulaciones";
    }

    @GetMapping("/freelancer/perfil/{id}")
    public String verPerfilFreelancerSoloLectura(@PathVariable Long id, Model model) {
        Optional<Freelancer> freelancerOpt = freelancerService.buscarPorId(id);
        if (freelancerOpt.isEmpty()) {
            return "redirect:/emple?perfilNoEncontrado=true";
        }

        Freelancer freelancer = freelancerOpt.get();

        model.addAttribute("freelancer", freelancer);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("habilidadesDisponibles", obtenerHabilidadesDisponibles());
        model.addAttribute("cvProcesado", false);
        model.addAttribute("modoEdicion", false);
        model.addAttribute("soloLectura", true);

        return "crearPerfil";
    }

    @GetMapping("/api/proyectos/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerProyectoPorId(@PathVariable Long id) {
        Optional<Proyecto> proyectoOpt = proyectoService.buscarPorId(id);
        if (proyectoOpt.isPresent()) {
            Proyecto proyecto = proyectoOpt.get();
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", proyecto.getId());
            datos.put("titulo", proyecto.getTitulo());
            datos.put("descripcion", proyecto.getDescripcion());
            datos.put("categoria", proyecto.getCategoria());
            datos.put("ubicacion", proyecto.getUbicacion());
            datos.put("presupuesto", proyecto.getPresupuesto());
            datos.put("modalidadPago", proyecto.getModalidadPago().name());
            datos.put("fechaInicio", proyecto.getFechaInicio());
            datos.put("fechaFinal", proyecto.getFechaFinal());
            datos.put("modalidad", proyecto.getModalidad());

            return ResponseEntity.ok(datos);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
        }
    }

    public List<String> obtenerIdiomasDisponibles() {
        return List.of("Español", "Inglés", "Francés", "Alemán");
    }

    public List<String> obtenerHabilidadesDisponibles() {
        return List.of(
                "Excel", "Photoshop", "Carpintería", "Atención al cliente", "Java", "Redacción",
                "Ventas", "Diseño gráfico", "SQL", "Manejo de maquinaria", "Community Management",
                "Electricidad", "Conducción", "Spring Boot", "React", "Angular", "Figma", "Docker",
                "Kubernetes", "Node", "HTML", "CSS", "JavaScript", "Python", "C#", "C++");
    }

    @GetMapping("/api/proyectos")
    @ResponseBody
    public List<Proyecto> obtenerProyectosPorCategoria(@RequestParam(required = false) String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return proyectoService.listarPorCategoriaYEstado(categoria, "Abierto");
        } else {
            return proyectoService.listarPorEstado("Abierto");
        }
    }
}
