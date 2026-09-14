package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

import com.sgb.mylibrum.entities.enums.TipoMaterial;

@Data
public class MaterialRequestDTO {
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    
    @NotNull(message = "A editora é obrigatória")
    private Long editoraId;
    
    private TipoMaterial tipo;
    private String subtitulo;
    private Long autorId;
    private String sinopse;
    private String issn;
    private String tema;
    private String descricao;
    private String observacao;
    @Size(max = 13, message = "O ISBN deve ter no máximo 13 caracteres")
    @Pattern(regexp = "\\d{1,13}", message = "O ISBN deve conter apenas dígitos (até 13)")
    private String isbn;
    private Integer edicao;
    private Integer anoPublicacao;
    private Integer quantidadePaginas;
    private Set<Long> autorIds;
    private Set<Long> generoIds;
}
