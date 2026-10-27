package com.sgb.mylibrum.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;

public final class UsuarioSpecification {

    private UsuarioSpecification() {
    }

    public static Specification<Usuario> filtrar(UsuarioFiltro filtro) {
        return AtivoSpecification.<Usuario>ativo(filtro.ativo())
                .and(AtivoSpecification.contemIgnoreCase("nome", filtro.nome()))
                .and(AtivoSpecification.<Usuario>igual("matriculaFuncionario", filtro.matriculaFuncionario()))
                .and(AtivoSpecification.contemIgnoreCase("email", filtro.email()))
                .and(AtivoSpecification.<Usuario>igual("cpf", filtro.cpf()))
                .and(AtivoSpecification.<Usuario>igual("funcao", filtro.funcao()))
                .and(AtivoSpecification.<Usuario>igual("filial.id", filtro.filialId()))
                .and(AtivoSpecification.<Usuario>igual("estaBloqueado", filtro.estaBloqueado()))
                .and(AtivoSpecification.contemIgnoreCase("cidade", filtro.cidade()));
    }
}
