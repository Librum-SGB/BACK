package com.sgb.mylibrum.integrations.isbn;

import java.util.List;

/** Dados de um livro obtidos de uma fonte externa, já normalizados. */
public record LivroExterno(
        String isbn,
        String titulo,
        String subtitulo,
        List<String> autores,
        String editora,
        Integer anoPublicacao,
        Integer quantidadePaginas,
        String sinopse,
        List<String> generos,
        String idioma,
        String capaUrl,
        String fonte) {

    public LivroExterno {
        autores = autores == null ? List.of() : List.copyOf(autores);
        generos = generos == null ? List.of() : List.copyOf(generos);
    }

    public boolean utilizavel() {
        return titulo != null && !titulo.isBlank();
    }
}
