package com.workeando.plataform.controller;

import com.workeando.plataform.model.ExperienciaLaboral;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Postulacion;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.model.Proyecto;
import com.workeando.plataform.service.FreelancerService;
import com.workeando.plataform.service.PostulacionService;
import com.workeando.plataform.service.ProyectoService;
import com.workeando.plataform.service.UsuarioService;
//import com.workeando.plataform.service.PostulacionService;

import jakarta.validation.Valid;

import com.workeando.plataform.service.CategoriaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.text.ParseException;
import java.util.Date;

@Controller
public class FreelancerController {

    private final ProyectoService proyectoService;
    private final FreelancerService freelancerService;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final PostulacionService postulacionService;

    public FreelancerController(ProyectoService proyectoService,
            FreelancerService freelancerService,
            UsuarioService usuarioService,
            CategoriaService categoriaService,
            PostulacionService postulacionService) {
        this.proyectoService = proyectoService;
        this.freelancerService = freelancerService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
        this.postulacionService = postulacionService;
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

    // Si no tiene perfil, asignamos uno por defecto
    if (!tienePerfil) {
        Freelancer perfilPorDefecto = new Freelancer();
        perfilPorDefecto.setUsuario(usuario);  
        perfilPorDefecto.setTelefono("");  
        perfilPorDefecto.setNivelEstudios("No especificado");  
        perfilPorDefecto.setLinkedin("");  
        perfilPorDefecto.setPortafolio("");  
        perfilPorDefecto.setEsPerfilPorDefecto(true);  

        // Guardamos el perfil por defecto
        freelancerService.guardarFreelancer(perfilPorDefecto);

        model.addAttribute("alertaPerfil", "Tu perfil ha sido creado automáticamente con información básica. ¡Completa tu perfil para mejorar tus posibilidades de ser elegido!");
        model.addAttribute("esPerfilPorDefecto", true);  // Perfil por defecto
    } else {
        Freelancer freelancer = freelancerService.buscarPorUsuario(usuario).get();
        if (freelancer.getEsPerfilPorDefecto()) {
            model.addAttribute("alertaPerfil", "Tu perfil ha sido creado automáticamente con información básica. ¡Completa tu perfil para mejorar tus posibilidades de ser elegido!");
            model.addAttribute("esPerfilPorDefecto", true);  // Perfil por defecto
        } else {
            model.addAttribute("esPerfilPorDefecto", false);  // Ya está completado
        }
    }

    // Obtener proyectos por categoría y estado "Abierto"
    List<Proyecto> proyectos;
    if (categoria != null && !categoria.isBlank()) {
        proyectos = proyectoService.listarPorCategoriaYEstado(categoria, "Abierto");
    } else {
        proyectos = proyectoService.listarPorEstado("Abierto");
    }

    model.addAttribute("proyectos", proyectos);
    model.addAttribute("categoriaSeleccionada", categoria);

    return "free";  
}



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

        // Solo agregar campos vacíos si no hay datos cargados
        if (freelancer.getIdiomas().isEmpty()) {
            freelancer.getIdiomas().add(new com.workeando.plataform.model.Idioma());
        }
        if (freelancer.getHabilidadesTecnicas().isEmpty()) {
            freelancer.getHabilidadesTecnicas().add(new com.workeando.plataform.model.HabilidadTecnica());
        }
        if (freelancer.getExperienciaLaboral().isEmpty()) {
            freelancer.getExperienciaLaboral().add(new com.workeando.plataform.model.ExperienciaLaboral());
        }

    model.addAttribute("soloLectura", false); // Freelancer puede editar

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

    // Validación: mínimo una categoría
    if (freelancer.getCategorias() == null || freelancer.getCategorias().isEmpty()) {
        result.rejectValue("categorias", "error.categorias", "Debes seleccionar al menos una categoría");
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("cvProcesado", true);
        return "crearPerfil";
    }

    // Validación: URL del portafolio (si existe)
    if (freelancer.getPortafolio() != null && !freelancer.getPortafolio().isBlank()) {
        String urlRegex = "^(https?://)?[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$";
        if (!freelancer.getPortafolio().matches(urlRegex)) {
            result.rejectValue("portafolio", "urlInvalida", "El portafolio debe ser una URL válida");
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "crearPerfil";
        }
    }

    // Validación: fechas de experiencia laboral
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

    // Obtener usuario autenticado
    String email = authentication.getName();
    Usuario usuario = usuarioService.buscarPorCorreo(email);

    // Buscar si ya existe perfil
    Optional<Freelancer> existenteOpt = freelancerService.buscarPorUsuario(usuario);
    Freelancer freelancerPersistente;

    if (existenteOpt.isPresent()) {
        // Modo edición
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

        // Actualiza el estado de perfil por defecto a falso si ya está completo
        freelancerPersistente.setEsPerfilPorDefecto(false);

    } else {
        // Modo creación
        freelancerPersistente = freelancer;
        freelancerPersistente.setUsuario(usuario);

        // Asignar el perfil por defecto si es el primer perfil
        freelancerPersistente.setEsPerfilPorDefecto(false); // Cambiar a false al completar el perfil
    }

    // Guardar perfil
    freelancerService.guardarFreelancer(freelancerPersistente);

    // Mensaje de éxito
    redirectAttributes.addFlashAttribute("toastExito", "Tu perfil ha sido guardado con éxito.");
    return "redirect:/free";  // Redirige a la página de inicio
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
            // 1. Extraer texto del CV usando Apache POI para mantener saltos de línea
            String textoExtraido = freelancerService.extraerTextoDesdeDocx(archivo.getInputStream());

            // 2. Obtener usuario autenticado
            String email = authentication.getName();
            Usuario usuario = usuarioService.buscarPorCorreo(email);

            // 3. Crear nuevo freelancer y asignar datos extraídos
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

            // 4. Preparar modelo para la vista del formulario
            model.addAttribute("freelancer", freelancer);
            model.addAttribute("categorias", categoriaService.listarTodas());
            model.addAttribute("habilidadesDisponibles", obtenerHabilidadesDisponibles());
            model.addAttribute("toastExito", "Datos del CV cargados. Por favor, completa o ajusta la información.");
            model.addAttribute("cvProcesado", true);

            return "crearPerfil"; // Mostrar la vista con los datos cargados

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
            return "redirect:/free"; // O una vista de error si prefieres
        }

        Freelancer freelancer = optFreelancer.get();
        List<Postulacion> postulaciones = postulacionService.buscarPorFreelancer(freelancer);

        model.addAttribute("postulaciones", postulaciones);
        return "misPostulaciones"; // sin extensión .html
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

        return "crearPerfil"; // reutilizamos el mismo formulario
    }

    // Endpoint REST: obtener detalles de un proyecto en formato JSON
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

    // Endpoint REST: filtrar proyectos por categoría
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
