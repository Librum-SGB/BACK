package com.sgb.mylibrum.dtos.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/** Só o ISBN é obrigatório; os demais campos, se informados, substituem o que veio da fonte externa. */
@Schema(description = "Dados para cadastrar um material por ISBN. Somente o ISBN é obrigatório; os campos preenchidos substituem os dados obtidos externamente.")
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
