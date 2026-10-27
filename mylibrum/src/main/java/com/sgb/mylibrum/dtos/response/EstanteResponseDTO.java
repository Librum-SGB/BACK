package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class EstanteResponseDTO {
    private Long id;
    private Long filialId;
    private String nome;
    private String localizacao;
    private Integer capacidade;
    private Integer qtdPrateleiras;
    private List<EstantePrateleiraResponseDTO> prateleiras;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}