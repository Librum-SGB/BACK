package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Set;

import com.sgb.mylibrum.entities.enums.TipoMaterial;

@Data
@Schema(description = "Material bibliográfico e seus vínculos com editora, autores e gêneros, incluindo quantidade de exemplares.")
public class MaterialResponseDTO {
    private Long id;
    private String titulo;
    private TipoMaterial tipo;
    private String subtitulo;
    private Long autorId;
    private String sinopse;
    private String issn;
    private String tema;
    private String descricao;
    private String observacao;
    private String isbn;
    private Integer edicao;
    private Integer anoPublicacao;
    private Integer quantidadePaginas;
    private Integer quantidadeExemplares;
    private Long editoraId;
    private Set<Long> autorIds;
    private Set<Long> generoIds;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}
