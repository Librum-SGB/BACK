package com.sgb.mylibrum.repositories.specifications;

import com.sgb.mylibrum.entities.EntidadeAuditavel;
import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class AtivoSpecification {

    private AtivoSpecification() {
    }

    public static <T extends EntidadeAuditavel> Specification<T> ativo(Boolean ativo) {
        return (root, query, criteriaBuilder) -> ativo == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("ativo"), ativo);
    }

    public static <T extends EntidadeAuditavel> Specification<T> contemIgnoreCase(String campo, String valor) {
        return (root, query, criteriaBuilder) -> valor == null || valor.isBlank()
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(campo)),
                        "%" + valor.trim().toLowerCase(Locale.ROOT) + "%");
    }

    public static <T extends EntidadeAuditavel> Specification<T> igual(String campo, Object valor) {
        return (root, query, criteriaBuilder) -> valor == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(caminho(root, campo), valor);
    }

    private static <T extends EntidadeAuditavel> jakarta.persistence.criteria.Path<?> caminho(
            jakarta.persistence.criteria.Root<T> root, String campo) {
        jakarta.persistence.criteria.Path<?> path = root;
        for (String parte : campo.split("\\.")) {
            path = path.get(parte);
        }
        return path;
    }
}
