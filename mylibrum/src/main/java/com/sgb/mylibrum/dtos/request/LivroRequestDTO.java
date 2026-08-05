package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class LivroRequestDTO {
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    
    @NotNull(message = "A editora é obrigatória")
    private Long editoraId;
    
    private String isbn13;
    private String isbn10;
    private Integer edicao;
    private Integer anoPublicacao;
    private Integer quantidadePaginas;
    private Set<Long> autorIds;
    private Set<Long> generoIds;
}