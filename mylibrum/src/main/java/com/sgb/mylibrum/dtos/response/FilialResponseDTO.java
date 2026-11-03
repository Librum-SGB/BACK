package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Data
@Schema(description = "Filial da biblioteca, com dados cadastrais, endereço, contato e situação.")
public class FilialResponseDTO {
    private Long id;
    private String nomeFantasia;
    private String razaoSocial;
    private String cnpj;
    private String inscricaoEstadual;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String telefone;
    private String email;
    private String horarioFuncionamento;
    private Boolean ativo;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}