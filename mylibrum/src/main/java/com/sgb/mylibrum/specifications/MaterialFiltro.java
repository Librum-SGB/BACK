package com.sgb.mylibrum.specifications;

import com.sgb.mylibrum.entities.enums.TipoMaterial;

public record MaterialFiltro(
        String titulo,
        String isbn,
        TipoMaterial tipo,
        Long editoraId,
        Long autorId,
        Long generoId,
        Integer anoPublicacaoDe,
        Integer anoPublicacaoAte,
        String tema,
        Boolean ativo) {
}
