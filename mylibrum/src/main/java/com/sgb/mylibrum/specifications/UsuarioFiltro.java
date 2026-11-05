package com.sgb.mylibrum.specifications;

import com.sgb.mylibrum.entities.enums.FuncaoUsuario;

public record UsuarioFiltro(
        String nome,
        String matriculaFuncionario,
        String email,
        String cpf,
        FuncaoUsuario funcao,
        Long filialId,
        Boolean estaBloqueado,
        String cidade,
        Boolean ativo) {
}
