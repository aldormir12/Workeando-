package com.workeando.plataform.service.impl;

import com.workeando.plataform.model.ExperienciaLaboral;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Usuario;
import com.workeando.plataform.repository.FreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Optional;

@Service
public class FreelancerServiceImpl {

    @Autowired
    private FreelancerRepository freelancerRepository;

    // Guarda o actualiza un perfil de freelancer.
    public void guardarFreelancer(Freelancer freelancer) {
        freelancerRepository.save(freelancer);
    }

    // Busca el perfil de un freelancer dado su usuario.

    public Optional<Freelancer> buscarPorUsuario(Usuario usuario) {
        return freelancerRepository.findByUsuario(usuario);
    }

    // Verifica si el usuario ya tiene un perfil de freelancer creado.

    public boolean perfilExiste(Usuario usuario) {
        return freelancerRepository.findByUsuario(usuario).isPresent();
    }

    public List<ExperienciaLaboral> extraerExperienciasLaborales(String texto) {
        List<ExperienciaLaboral> experiencias = new ArrayList<>();

        // Preprocesar: quitar saltos excesivos y normalizar espacios
        texto = texto.replaceAll("\\r", "")
                .replaceAll("\\n{2,}", "\n")
                .replaceAll("\\s{2,}", " ")
                .trim();

        // Dividir por secciones que parecen experiencias (e.g., por título o años)
        Pattern bloquePattern = Pattern.compile(
                "(?:(?i)([A-Z][\\w\\s&\\.,]+)\\n)?" + // Posible nombre empresa o título
                        "(.{0,100})\\n" + // Posible puesto o función
                        "((?:\\d{2}/\\d{4}|[A-Za-z]+ \\d{4}|\\d{4}) ?[-–] ?(?:\\d{2}/\\d{4}|[A-Za-z]+ \\d{4}|\\d{4}|actualidad))"
                        + // Fechas
                        "\\n(.{10,500})", // Descripción de funciones o logros
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = bloquePattern.matcher(texto);

        while (matcher.find()) {
            ExperienciaLaboral exp = new ExperienciaLaboral();

            String empresa = matcher.group(1) != null ? matcher.group(1).trim() : "Empresa no especificada";
            String puesto = matcher.group(2).trim();
            String periodo = matcher.group(3).trim();
            String descripcion = matcher.group(4).trim();

            exp.setEmpresa(empresa);
            exp.setPuesto(puesto);
            exp.setPeriodo(periodo);
            exp.setDescripcion(descripcion);

            experiencias.add(exp);
        }

        // Si no se detecta nada, puedes agregar un ejemplo ficticio o log
        if (experiencias.isEmpty()) {
            System.out.println("No se detectaron bloques de experiencia laboral automáticamente.");
        }

        return experiencias;
    }

    public List<String> extraerIdiomas(String texto) {
        List<String> posiblesIdiomas = List.of("Español", "Inglés", "Portugués", "Francés", "Alemán", "Italiano",
                "Chino", "Japonés");
        List<String> idiomasDetectados = new ArrayList<>();

        for (String idioma : posiblesIdiomas) {
            if (texto.toLowerCase().contains(idioma.toLowerCase())) {
                idiomasDetectados.add(idioma);
            }
        }

        return idiomasDetectados;
    }

    public List<String> extraerHabilidades(String texto) {
        List<String> posiblesHabilidades = List.of(
                "Java", "Spring", "Python", "JavaScript", "React", "Angular", "SQL", "Docker",
                "AWS", "Git", "HTML", "CSS", "Node.js", "C#", "Kubernetes", "MongoDB", "PostgreSQL");
        List<String> habilidadesDetectadas = new ArrayList<>();

        for (String skill : posiblesHabilidades) {
            if (texto.toLowerCase().contains(skill.toLowerCase())) {
                habilidadesDetectadas.add(skill);
            }
        }

        return habilidadesDetectadas;
    }

}
