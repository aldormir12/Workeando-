package com.workeando.plataform.service;

import com.workeando.plataform.model.ExperienciaLaboral;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.HabilidadTecnica;
import com.workeando.plataform.model.Idioma;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.repository.FreelancerRepository;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Set;

@Service
public class FreelancerService {

    @Autowired
    private FreelancerRepository freelancerRepository;

public void guardarFreelancer(Freelancer freelancer) {
    System.out.println("GUARDANDO FREELANCER: " + freelancer);

    // Si es un perfil por defecto, se guarda sin validación
    if (freelancer.getEsPerfilPorDefecto()) {
        // Desactivar la validación para perfiles por defecto
        freelancer.setTelefono("000000");  // Teléfono válido para evitar violación de validación
        freelancer.setNivelEstudios("No especificado"); // Aseguramos que haya un valor por defecto

        freelancerRepository.save(freelancer);
        freelancerRepository.flush();
        System.out.println("Perfil por defecto guardado sin validación.");
    } else {
        // Si no es un perfil por defecto, se guarda con la validación normal
        freelancerRepository.save(freelancer);
        freelancerRepository.flush();
        System.out.println("Perfil con validación guardado.");
    }
}

    // Busca el perfil de un freelancer dado su usuario.
    public Optional<Freelancer> buscarPorUsuario(Usuario usuario) {
        return freelancerRepository.findByUsuario(usuario);
    }

    // Verifica si el usuario ya tiene un perfil de freelancer creado.
    public boolean perfilExiste(Usuario usuario) {
        return freelancerRepository.findByUsuario(usuario).isPresent();
    }

    public Freelancer buscarPorCorreoUsuario(String correo) {
    return freelancerRepository.findByUsuarioCorreo(correo)
        .orElseThrow(() -> new RuntimeException("No se encontró freelancer con el correo: " + correo));
}

    // EXPERIENCIA LABORAL
    public List<ExperienciaLaboral> extraerExperienciasLaborales(String texto) {
        List<ExperienciaLaboral> experiencias = new ArrayList<>();

        // Normalización: eliminar \r, conservar \n y recortar
        texto = texto.replace("\r", "").trim();

        // empresa, puesto, fechas y descripción multilínea
        Pattern pattern = Pattern.compile(
                "(?m)^(.+?)\\n" + // Empresa (línea 1)
                        "^(.+?)\\n" + // Puesto (línea 2)
                        "^(\\d{2}/\\d{4})\\s*[-–]\\s*(\\d{2}/\\d{4}|actualidad)\\n" + // Fechas (línea 3)
                        "((?:^.+\\n?)+?)" // Descripción (línea(s) 4+)
                , Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(texto);

        while (matcher.find()) {
            ExperienciaLaboral exp = new ExperienciaLaboral();
            exp.setEmpresa(matcher.group(1).trim());
            exp.setPuesto(matcher.group(2).trim());
            exp.setFechaDesde(matcher.group(3).trim());
            exp.setFechaHasta(matcher.group(4).trim());

            // Limpia la descripción de posibles saltos finales
            String descripcion = matcher.group(5).trim().replaceAll("\\n{2,}", "\n");
            exp.setDescripcion(descripcion);

            System.out.println("Experiencia detectada:");
            System.out.println("Empresa: " + exp.getEmpresa());
            System.out.println("Puesto: " + exp.getPuesto());
            System.out.println("Desde: " + exp.getFechaDesde());
            System.out.println("Hasta: " + exp.getFechaHasta());
            System.out.println("Descripción: " + exp.getDescripcion());

            experiencias.add(exp);
        }

        System.out.println("🔎 Total de experiencias extraídas: " + experiencias.size());
        return experiencias;
    }

    // telefono
    public String extraerTelefono(String texto) {
        Pattern pattern = Pattern.compile("(\\+\\d{1,3}\\s?)?\\d{6,14}");
        Matcher matcher = pattern.matcher(texto);
        return matcher.find() ? matcher.group() : "";
    }

    public String extraerEnlaceLinkedin(String texto) {
        Pattern pattern = Pattern.compile("https?://(www\\.)?linkedin\\.com/[\\w\\-/]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texto);
        return matcher.find() ? matcher.group() : "";
    }

    public String extraerEnlacePortafolio(String texto) {
        Pattern pattern = Pattern.compile("https?://(www\\.)?(behance\\.net|dribbble\\.com|github\\.com)/[\\w\\-/]+",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texto);
        return matcher.find() ? matcher.group() : "";
    }

    public List<Idioma> extraerIdiomasComoObjetos(String texto) {
        List<Idioma> lista = new ArrayList<>();

        // Normalización
        texto = texto.toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .replace("Ñ", "n")
                .replace("–", "-")
                .replace("—", "-")
                .trim();

        // Mapeo extendido de niveles
        Map<String, Idioma.Nivel> niveles = Map.ofEntries(
                Map.entry("basico", Idioma.Nivel.BASICO),
                Map.entry("intermedio", Idioma.Nivel.INTERMEDIO),
                Map.entry("avanzado", Idioma.Nivel.AVANZADO),
                Map.entry("nativo", Idioma.Nivel.NATIVO),
                Map.entry("c1", Idioma.Nivel.C1_AVANZADO),
                Map.entry("c1-avanzado", Idioma.Nivel.C1_AVANZADO),
                Map.entry("c1 - avanzado", Idioma.Nivel.C1_AVANZADO),
                Map.entry("c2", Idioma.Nivel.C2_EXPERTO),
                Map.entry("c2-experto", Idioma.Nivel.C2_EXPERTO),
                Map.entry("c2 - experto", Idioma.Nivel.C2_EXPERTO));

        // Pattern robusto para detectar idioma y nivel
        Pattern pattern = Pattern.compile(
                "(espanol|ingles|frances|portugues|aleman|italiano|chino|japones|ruso)\\s*[:\\-]?\\s*(nativo|basico|intermedio|avanzado|c1\\s*-\\s*avanzado|c2\\s*-\\s*experto|c1|c2)?",
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(texto);
        Set<String> detectados = new HashSet<>();

        while (matcher.find()) {
            String idiomaRaw = matcher.group(1);
            String nivelRaw = matcher.group(2) != null ? matcher.group(2).replaceAll("\\s+", "").toLowerCase() : "";

            String idioma = idiomaRaw.trim().toLowerCase();
            if (detectados.contains(idioma))
                continue;
            detectados.add(idioma);

            Idioma idiomaObj = new Idioma();
            idiomaObj.setNombre(capitalizarIdioma(idioma));

            Idioma.Nivel nivelEnum = niveles.getOrDefault(nivelRaw, Idioma.Nivel.BASICO);
            idiomaObj.setNivel(nivelEnum);

            System.out.println("Idioma detectado: " + idiomaObj.getNombre() + " - Nivel: " + idiomaObj.getNivel());
            lista.add(idiomaObj);
        }

        System.out.println("Total de idiomas extraídos: " + lista.size());
        return lista;
    }

    private String capitalizarIdioma(String idioma) {
        switch (idioma.toLowerCase()) {
            case "espanol":
                return "Español";
            case "ingles":
                return "Inglés";
            case "frances":
                return "Francés";
            case "portugues":
                return "Portugués";
            case "aleman":
                return "Alemán";
            case "italiano":
                return "Italiano";
            case "chino":
                return "Chino";
            case "japones":
                return "Japonés";
            case "ruso":
                return "Ruso";
            default:
                return idioma;
        }
    }

  public List<HabilidadTecnica> extraerHabilidadesComoObjetos(String texto) {
    List<HabilidadTecnica> lista = new ArrayList<>();

    String textoNorm = texto.toLowerCase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
            .replace("–", "-")
            .replace("—", "-")
            .replaceAll("[\\t\\r\\n]+", " "); // Limpia saltos de línea/tabulaciones

    String[] habilidades = {
        "excel", "photoshop", "java", "sql", "spring boot", "javascript", "html", "css", "react", "angular", "node",
        "python", "c#", "c++", "docker", "kubernetes", "git", "wordpress", "autocad", "community management",
        "figma", "illustrator", "conduccion", "electricidad", "carpinteria", "redaccion", "ventas"
    };

    Map<String, String> nivelesMap = Map.of(
        "basico", "Básico",
        "intermedio", "Intermedio",
        "avanzado", "Avanzado",
        "experto", "Experto"
    );

    Set<String> detectadas = new HashSet<>();

    for (String hab : habilidades) {
        // Regex que tolera "Habilidad: Nivel" o "Habilidad - Nivel"
        Pattern p = Pattern.compile(hab + "\\s*[:\\-\\(]?\\s*(basico|intermedio|avanzado|experto)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(textoNorm);

        if (m.find()) {
            String nombre = capitalizarHabilidad(hab); // ✅ capitaliza bien múltiples palabras
            if (detectadas.contains(nombre)) continue;
            detectadas.add(nombre);

            String nivelRaw = m.group(1);
            String nivelFinal = nivelesMap.getOrDefault(nivelRaw.toLowerCase(), "No especificado");

            HabilidadTecnica h = new HabilidadTecnica();
            h.setNombre(nombre);
            h.setNivel(nivelFinal);

            System.out.println("Habilidad detectada: " + h.getNombre() + " - Nivel: " + h.getNivel());
            lista.add(h);
        }
    }

    System.out.println("Total habilidades extraídas: " + lista.size());
    return lista;
}

// Función que capitaliza cada palabra
private String capitalizarHabilidad(String input) {
    if (input == null || input.isBlank()) return input;

    return Arrays.stream(input.split(" "))
            .map(p -> p.substring(0, 1).toUpperCase() + p.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
}



  
    public Map<String, String> extraerEnlacesCV(String texto) {
        Map<String, String> enlaces = new HashMap<>();

        // Normalización
        texto = texto.toLowerCase()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");

        // Buscar LinkedIn
        Pattern linkedinPattern = Pattern.compile("https?://(www\\.)?linkedin\\.com/[\\w\\-/]+",
                Pattern.CASE_INSENSITIVE);
        Matcher linkedinMatcher = linkedinPattern.matcher(texto);
        if (linkedinMatcher.find()) {
            String link = linkedinMatcher.group();
            enlaces.put("linkedin", link);
            System.out.println("LinkedIn detectado: " + link);
        }

        // Buscar Portafolio: dominios válidos y excluir LinkedIn y Gmail
        Pattern portafolioPattern = Pattern.compile(
                "(https?://)?(www\\.)?(behance\\.net|dribbble\\.com|github\\.com|figma\\.com|notion\\.site|[\\w\\-]+\\.(dev|studio|design|me))(/[\\w\\-./]*)?",
                Pattern.CASE_INSENSITIVE);

        Matcher portafolioMatcher = portafolioPattern.matcher(texto);
        while (portafolioMatcher.find()) {
            String portafolio = portafolioMatcher.group();

            // Evitar falsos positivos
            if (portafolio.contains("linkedin.com") || portafolio.contains("gmail.com"))
                continue;

            if (!portafolio.startsWith("http")) {
                portafolio = "https://" + portafolio;
            }

            // Solo guardar el primero válido
            if (!enlaces.containsKey("portafolio")) {
                enlaces.put("portafolio", portafolio);
                System.out.println("Portafolio detectado: " + portafolio);
            }
        }

        return enlaces;
    }

    public String extraerTextoDesdeDocx(InputStream inputStream) {
        try (XWPFDocument document = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

            String textoExtraido = extractor.getText();

            System.out.println("------ TEXTO EXTRAÍDO ------");
            System.out.println(textoExtraido);
            System.out.println("------ FIN DEL TEXTO ------");

            return textoExtraido;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public Optional<Freelancer> buscarPorId(Long id) {
    return freelancerRepository.findById(id);
}

   // Método para crear un perfil por defecto y evitar la validación de teléfono
    public void crearPerfilPorDefecto(Usuario usuario) {
        Freelancer perfilPorDefecto = new Freelancer();
        perfilPorDefecto.setUsuario(usuario);  // Asociar al usuario autenticado
        perfilPorDefecto.setTelefono("123456");  // Teléfono de ejemplo válido
        perfilPorDefecto.setNivelEstudios("No especificado");
        perfilPorDefecto.setLinkedin("");  // LinkedIn vacío
        perfilPorDefecto.setPortafolio("");  // Portafolio vacío
        perfilPorDefecto.setEsPerfilPorDefecto(true);  // Indicar que es un perfil por defecto

        // Guardar el perfil por defecto
        guardarFreelancer(perfilPorDefecto);
    }

}
