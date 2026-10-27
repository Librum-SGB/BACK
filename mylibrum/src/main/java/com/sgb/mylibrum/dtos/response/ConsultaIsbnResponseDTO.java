package com.sgb.mylibrum.dtos.response;

import java.util.List;

import com.sgb.mylibrum.integrations.isbn.LivroExterno;

/**
 * Prévia da consulta por ISBN (nada é gravado).
 * Se {@code jaCadastrado}, {@code material} traz o registro local; senão {@code livro} traz os dados externos
 * (nulo se nenhuma fonte encontrou) e as listas "novos*" indicam o que seria criado.
 */
public record ConsultaIsbnResponseDTO(
        String isbn,
        boolean jaCadastrado,
        MaterialResponseDTO material,
        boolean encontradoExternamente,
        LivroExterno livro,
        boolean criariaEditora,
        List<String> novosAutores,
        List<String> novosGeneros) {
}
