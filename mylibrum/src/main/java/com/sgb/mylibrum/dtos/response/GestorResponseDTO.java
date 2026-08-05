package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class GestorResponseDTO {
    private Long id;
    private String login;
    private String matriculaFuncionario;
    private Long filialId;
    private OffsetDateTime ultimoAcesso;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}