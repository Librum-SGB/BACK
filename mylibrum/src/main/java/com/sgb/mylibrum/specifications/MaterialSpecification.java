package com.sgb.mylibrum.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;

import jakarta.persistence.criteria.JoinType;

public final class MaterialSpecification {

    private MaterialSpecification() {
    }

    public static Specification<Material> filtrar(MaterialFiltro filtro) {
        FiltrosUtil.validarIntervalo(filtro.anoPublicacaoDe(), filtro.anoPublicacaoAte(), "anoPublicacao");
        return AtivoSpecification.<Material>ativo(filtro.ativo())
                .and(AtivoSpecification.contemIgnoreCase("titulo", filtro.titulo()))
                .and(AtivoSpecification.contemIgnoreCase("isbn", filtro.isbn()))
                .and(AtivoSpecification.contemIgnoreCase("tema", filtro.tema()))
                .and(AtivoSpecification.<Material>igual("tipo", filtro.tipo()))
                .and(AtivoSpecification.<Material>igual("editora.id", filtro.editoraId()))
                .and(FiltrosUtil.<Material, Integer>entre(
                        "anoPublicacao", filtro.anoPublicacaoDe(), filtro.anoPublicacaoAte()))
                .and(autor(filtro.autorId()))
                .and(genero(filtro.generoId()));
    }

    // Autor principal ou qualquer autor da relação N:M
    private static Specification<Material> autor(Long autorId) {
        return (root, query, cb) -> {
            if (autorId == null) {
                return cb.conjunction();
            }
            query.distinct(true);
            var autores = root.join("autores", JoinType.LEFT);
            return cb.or(cb.equal(root.get("autor").get("id"), autorId), cb.equal(autores.get("id"), autorId));
        };
    }

    private static Specification<Material> genero(Long generoId) {
        return (root, query, cb) -> {
            if (generoId == null) {
                return cb.conjunction();
            }
            query.distinct(true);
            return cb.equal(root.join("generos", JoinType.INNER).get("id"), generoId);
        };
    }
}
