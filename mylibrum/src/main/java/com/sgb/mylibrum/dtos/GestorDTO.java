package com.sgb.mylibrum.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestorDTO {
    private Long id;
    private String login;
    private String senha;
    private String matriculaFuncionario;
    private OffsetDateTime ultimoAcesso;
    private Long filialId;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}