package com.sgb.mylibrum.dtos.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

/** Só o ISBN é obrigatório; os demais campos, se informados, substituem o que veio da fonte externa. */
public record CadastroIsbnRequestDTO(
        @NotBlank(message = "ISBN é obrigatório") String isbn,
        String titulo,
        String subtitulo,
        String editora,
        List<String> autores,
        List<String> generos,
        Integer anoPublicacao,
        Integer quantidadePaginas,
        String sinopse) {
}
