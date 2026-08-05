package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class LivroResponseDTO {
    private Long id;
    private String titulo;
    private String isbn13;
    private String isbn10;
    private Integer edicao;
    private Integer anoPublicacao;
    private Integer quantidadePaginas;
    private Long editoraId;
    private Set<Long> autorIds;
    private Set<Long> generoIds;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}