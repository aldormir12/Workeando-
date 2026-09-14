package com.workeando.plataform.controller;

import com.workeando.plataform.model.ExperienciaLaboral;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.service.impl.CategoriaServiceImpl;
import com.workeando.plataform.service.impl.FreelancerServiceImpl;
import com.workeando.plataform.service.impl.ProyectoServiceImpl;
import com.workeando.plataform.service.impl.UsuarioServiceImpl;
import com.workeando.plataform.model.Proyecto;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.text.ParseException;
import java.util.Date;


@Controller
public class FreelancerController {

    private final ProyectoServiceImpl proyectoService;
    private final FreelancerServiceImpl freelancerService;
    private final UsuarioServiceImpl usuarioService;
    private final CategoriaServiceImpl categoriaService;

    public FreelancerController(ProyectoServiceImpl proyectoService,
            FreelancerServiceImpl freelancerService,
            UsuarioServiceImpl usuarioService,
            CategoriaServiceImpl categoriaService) {
        this.proyectoService = proyectoService;
        this.freelancerService = freelancerService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    // Vista principal de proyectos para el freelancer
    @GetMapping("/free")
    public String freelancerPage(@RequestParam(required = false) String categoria,
            Model model,
            Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorCorreo(email);
        model.addAttribute("nombre", usuario.getNombre());

        // Verifica si el freelancer ya tiene perfil
        boolean tienePerfil = freelancerService.perfilExiste(usuario);
        model.addAttribute("tienePerfil", tienePerfil);

        // Obtener proyectos por categoría y estado "abierto"
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

    // Mostrar formulario de creación/edición de perfil
    @GetMapping("/free/perfil")
    public String mostrarFormularioPerfil(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorCorreo(email);

        Freelancer freelancer = freelancerService.buscarPorUsuario(usuario)
                .orElse(new Freelancer());

        if (freelancer.getUsuario() == null) {
            freelancer.setUsuario(usuario);
        }

        // Agregar categorías y listas de opciones
        model.addAttribute("freelancer", freelancer);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("idiomasDisponibles", List.of("Inglés", "Español", "Francés", "Alemán", "Portugués",
                "Italiano", "Japonés", "Árabe", "Ruso", "Chino mandarín"));
        model.addAttribute("habilidadesDisponibles", List.of(
                "Java", "Python", "JavaScript", "SQL", "HTML/CSS", "C++", "Ruby", "PHP", "Node.js", "Excel (Avanzado)",
                "Power BI",
                "Diseño gráfico (Photoshop, Illustrator)", "Carpintería", "Reparación de electrodomésticos",
                "Gestión de proyectos (MS Project, Asana)",
                "Contabilidad y Finanzas", "Marketing Digital (SEO, SEM, Google Ads)",
                "Redes sociales (Gestión, Publicidad)",
                "Administración de sistemas", "Electricidad básica"));
        model.addAttribute("habilidadesBlandasDisponibles",
                List.of("Comunicación efectiva", "Trabajo en equipo", "Resolución de conflictos", "Liderazgo",
                        "Creatividad", "Gestión del tiempo", "Empatía", "Negociación", "Pensamiento crítico",
                        "Adaptabilidad", "Toma de decisiones", "Atención al cliente"));
        model.addAttribute("nivelesEstudiosDisponibles", List.of("Secundaria completa", "Técnico medio", "Licenciatura",
                "Maestría", "Doctorado", "Certificados y cursos específicos"));

        return "crearPerfil";
    }

    // Procesar formulario de perfil
    @PostMapping("/free/perfil")
    public String guardarPerfil(@ModelAttribute("freelancer") @Valid Freelancer freelancer,
            BindingResult result,
            Authentication authentication,
            Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
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

   
     // Validación de formato de fechas en la experiencia laboral
for (int i = 0; i < freelancer.getExperienciaLaboral().size(); i++) {
    ExperienciaLaboral exp = freelancer.getExperienciaLaboral().get(i);
    String fechaRegex = "^\\d{2}/\\d{4}$";  // Ejemplo: 02/2025

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

    if (exp.getFechaDesde() != null && !exp.getFechaDesde().format(formatter).matches(fechaRegex)) {
        result.rejectValue("experienciaLaboral[" + i + "].fechaDesde", "fechaInvalida", "La fecha de inicio debe tener el formato MM/YYYY");
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "crearPerfil";
    }

    if (exp.getFechaHasta() != null && !exp.getFechaHasta().format(formatter).matches(fechaRegex)) {
        result.rejectValue("experienciaLaboral[" + i + "].fechaHasta", "fechaInvalida", "La fecha de fin debe tener el formato MM/YYYY");
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "crearPerfil";
    }

    if (exp.getFechaDesde() != null && exp.getFechaHasta() != null) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

            String fechaDesdeStr = exp.getFechaDesde().format(formatter);
            String fechaHastaStr = exp.getFechaHasta().format(formatter);

            Date fechaDesde = sdf.parse(fechaDesdeStr);
            Date fechaHasta = sdf.parse(fechaHastaStr);

            if (fechaHasta.before(fechaDesde)) {
                result.rejectValue("experienciaLaboral[" + i + "].fechaHasta", "fechaInvalida", "La fecha de fin no puede ser anterior a la fecha de inicio");
                model.addAttribute("categorias", categoriaService.listarTodas());
                return "crearPerfil";
            }

            if (fechaHasta.after(new Date())) {
                result.rejectValue("experienciaLaboral[" + i + "].fechaHasta", "fechaInvalida", "La fecha de fin no puede ser posterior al día actual");
                model.addAttribute("categorias", categoriaService.listarTodas());
                return "crearPerfil";
            }

        } catch (ParseException e) {
            result.rejectValue("experienciaLaboral[" + i + "].fechaHasta", "fechaInvalida", "Error al procesar las fechas");
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "crearPerfil";
        }
    }
}


        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarPorCorreo(email);
        freelancer.setUsuario(usuario);

        freelancerService.guardarFreelancer(freelancer);
        redirectAttributes.addFlashAttribute("toastExito", "Tu perfil ha sido guardado con éxito.");
        return "redirect:/free";
    }

    /*
     * @PostMapping("/free/subirCv")
     * public String procesarCv(@RequestParam("cvFile") MultipartFile archivo,
     * Authentication authentication,
     * RedirectAttributes redirectAttributes) {
     * 
     * if (archivo.isEmpty()) {
     * redirectAttributes.addFlashAttribute("toastError",
     * "Por favor, selecciona un archivo.");
     * return "redirect:/free";
     * }
     * 
     * try {
     * // 1. Extraer texto del CV
     * Tika tika = new Tika();
     * String textoExtraido = tika.parseToString(archivo.getInputStream());
     * 
     * // 2. Obtener usuario autenticado
     * String email = authentication.getName();
     * Usuario usuario = usuarioService.buscarPorCorreo(email);
     * 
     * // 3. Buscar o crear perfil del freelancer
     * Freelancer freelancer =
     * freelancerService.buscarPorUsuario(usuario).orElse(new Freelancer());
     * freelancer.setUsuario(usuario);
     * 
     * // 4. Extraer y asignar campos del CV
     * freelancer.setDescripcion(extraerResumen(textoExtraido));
     * freelancer.setTelefono(extraerTelefono(textoExtraido));
     * freelancer.setPortafolio(""); // Puedes implementar un método que detecte
     * URLs
     * 
     * // 5. Extraer experiencias como lista de objetos
     * List<ExperienciaLaboral> experiencias =
     * extraerExperienciasLaborales(textoExtraido);
     * freelancer.setExperienciaLaboral(experiencias);
     * 
     * // 6. Guardar el archivo del CV
     * freelancer.setCvArchivo(archivo.getBytes());
     * 
     * // 7. Guardar perfil
     * freelancerService.guardarFreelancer(freelancer);
     * 
     * redirectAttributes.addFlashAttribute("toastExito",
     * "Datos extraídos y perfil actualizado.");
     * } catch (Exception e) {
     * e.printStackTrace();
     * redirectAttributes.addFlashAttribute("toastError",
     * "Error al procesar el archivo.");
     * }
     * 
     * return "redirect:/free";
     * }
     */

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

    // Ejemplo de cómo puedes obtener los idiomas disponibles
    public List<String> obtenerIdiomasDisponibles() {
        // Puedes reemplazar esto con una consulta a la base de datos o una lista
        // estática
        return List.of("Español", "Inglés", "Francés", "Alemán");
    }

    // Ejemplo de cómo puedes obtener las habilidades disponibles
    public List<String> obtenerHabilidadesDisponibles() {
        // Reemplazar por los datos reales de habilidades
        return List.of("Java", "Python", "HTML", "CSS", "JavaScript");
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
