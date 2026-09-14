package com.workeando.plataform.service;

import com.workeando.plataform.model.Categoria;
import com.workeando.plataform.model.Freelancer;
import com.workeando.plataform.model.Proyecto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FypService {

    @PersistenceContext
    private EntityManager em;

    private final FreelancerService freelancerService;

    public FypService(FreelancerService freelancerService) {
        this.freelancerService = freelancerService;
    }

    // Feed basado en categorías del freelancer. Excluye proyectos ya postulados.
    public List<Proyecto> feedParaUsuario(String correoFreelancer, int limit) {
        Freelancer freelancer = freelancerService.buscarPorCorreoUsuario(correoFreelancer);
        if (freelancer == null) {
            return Collections.emptyList();
        }

        // Tomar las categorías del freelancer como entidades, no IDs
        Set<Categoria> categorias = freelancer.getCategorias() == null
                ? Collections.emptySet()
                : new HashSet<>(freelancer.getCategorias());

        if (categorias.isEmpty()) {
            return Collections.emptyList();
        }

        // Excluir proyectos ya postulados por el correo del freelancer
        Set<Long> excluir = postulacionesIds(correoFreelancer);

        if (excluir.isEmpty()) {
            return em.createQuery(
                    "select pr " +
                            "from Proyecto pr " +
                            "where pr.estado = :estado " +
                            "  and pr.categoria in :categorias " +
                            "order by pr.fechaPublicacion desc",
                    Proyecto.class)
                    .setParameter("estado", "Abierto")
                    .setParameter("categorias", categorias)
                    .setMaxResults(limit)
                    .getResultList();
        } else {
            return em.createQuery(
                    "select pr " +
                            "from Proyecto pr " +
                            "where pr.estado = :estado " +
                            "  and pr.categoria in :categorias " +
                            "  and pr.id not in :excluir " +
                            "order by pr.fechaPublicacion desc",
                    Proyecto.class)
                    .setParameter("estado", "Abierto")
                    .setParameter("categorias", categorias)
                    .setParameter("excluir", excluir)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    // Ids de proyectos ya postulados por el correo del freelancer
    private Set<Long> postulacionesIds(String correoFreelancer) {
        List<Long> ids = em.createQuery(
                "select p.proyecto.id " +
                        "from Postulacion p " +
                        "where p.correoFreelancer = :correo",
                Long.class)
                .setParameter("correo", correoFreelancer)
                .getResultList();
        return new HashSet<>(ids);
    }
}