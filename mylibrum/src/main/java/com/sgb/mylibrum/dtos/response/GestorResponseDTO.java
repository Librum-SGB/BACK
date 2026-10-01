package com.sgb.mylibrum.dtos.response;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class GestorResponseDTO {
    private Long id;
    private String email;
    private String matriculaFuncionario;
    private Long filialId;
    private OffsetDateTime ultimoAcesso;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}