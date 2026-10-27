package com.sgb.mylibrum.integrations.isbn;

import java.util.Optional;

public interface FonteIsbn {

    String nome();

    /** Indica se a fonte pode ser consultada com as chaves informadas (fontes pagas exigem chave). */
    boolean disponivel(ChavesIsbn chaves);

    /** Busca pelo ISBN-13 normalizado. Vazio quando não encontrado; pode lançar exceção em falhas de rede/HTTP. */
    Optional<LivroExterno> buscar(String isbn13, ChavesIsbn chaves);
}
